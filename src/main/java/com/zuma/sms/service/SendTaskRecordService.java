package com.zuma.sms.service;

import com.zuma.sms.api.SendTaskManager;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.entity.SmsContent;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.enums.db.IsDeleteEnum;
import com.zuma.sms.enums.db.SendTaskRecordStatusEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.SendTaskRecordAddForm;
import com.zuma.sms.form.SendTaskRecordUpdateForm;
import com.zuma.sms.form.WarnByDateRangeForm;
import com.zuma.sms.repository.SendTaskRecordRepository;
import com.zuma.sms.util.DateUtil;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 12:19
 * 发送任务记录
 */
@Service
@Slf4j
public class SendTaskRecordService {
	@Autowired
	private SendTaskRecordRepository sendTaskRecordRepository;
	@Autowired
	private PageRequestFactory pageRequestFactory;
	@Autowired
	private ChannelStore channelStore;
	@Autowired
	private NumberGroupService numberGroupService;
	@Autowired
	private SmsContentService smsContentService;

	@Autowired
	private SendTaskManager sendTaskManager;

	@Autowired
	private ConfigStore configStore;


	/**
	 * 累加指定id的 成功号码数和失败号码数
	 */
	@Transactional
	public SendTaskRecord incrementSuccessAndFailedNumById(int successNum, int failedNum,long taskId) {
		sendTaskRecordRepository.updateSuccessAndFailedNum(successNum,failedNum,taskId);
		return findOne(taskId);
	}


	/**
	 * 判断指定时间范围内.线程是否过多,需要预警
	 */
	public boolean warnByDateRange(WarnByDateRangeForm form) {
		//查询当前 未结束的所有任务
		List<SendTaskRecord> list = sendTaskRecordRepository.findAllByStatusIn(new Integer[]{SendTaskRecordStatusEnum.RUN.getCode(),
				SendTaskRecordStatusEnum.PAUSE.getCode(),
				SendTaskRecordStatusEnum.WAIT.getCode()});
		//号码总数
		long phoneNum = 0;
		//线程总数
		int threadSum = 0;
		for (SendTaskRecord item : list) {
			if(DateUtil.isCoincide(item.getExpectStartTime(), item.getExpectEndTime(),
					form.getStartTime(),form.getEndTime())){
				phoneNum += item.getNumberNum();
				threadSum += item.getThreadCount();
			}

		}
		//超出限制 , 则返回true
		return phoneNum > configStore.sendTaskWarnOfPhoneNum || threadSum > configStore.sendTaskWarnOfThreadNum;
	}


	/**
	 * 恢复启动任务
	 * 暂不支持. 一个问题. 区分手动暂停和分时任务暂停
	 */
	public void rerunTask(long id) {
		sendTaskManager.rerunTask(id);
	}

	/**
	 * 暂停任务
	 */
	public void pauseTask(long id, long time) {
		sendTaskManager.pauseTask(id,time);
	}

	/**
	 * 根据id获取号码文件输入流
	 * 需要自行校验id是否存在
	 */
	public BufferedInputStream getInputStream(long id) {
		try {
			return new BufferedInputStream(new FileInputStream(getFile(id)));
		} catch (FileNotFoundException e) {
			log.error("[numberSource]发送任务异常文件不存在.id:{}",id);
			throw new SmsSenderException(ErrorEnum.NUMBER_SOURCE_FILE_NOT_EXIST);
		}
	}

	/**
	 * 根据id获取号码文件file
	 */
	public File getFile(Long id) {
		return new File(configStore.sendTaskErrorInfoPre + id + ".txt");
	}

	/**
	 * 模糊查询
	 */
	public PageVO<SendTaskRecord> searchByName(String name, IsDeleteEnum isDeleteEnum) {
		Pageable pageable = pageRequestFactory.buildForLikeSearch();
		Page<SendTaskRecord> page = sendTaskRecordRepository.findByNameContainingAndIsDeleteEquals(name, isDeleteEnum.getCode(), pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 查询单个
	 */
	public SendTaskRecord findOne(long id) {
		SendTaskRecord result = sendTaskRecordRepository.findOne(id);
		if (result == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return result;
	}

	/**
	 * 分页查询
	 */
	public PageVO<SendTaskRecord> findPage(Pageable pageable, IsDeleteEnum isDeleteEnum) {
		Page<SendTaskRecord> page = sendTaskRecordRepository.findAllByIsDelete(isDeleteEnum.getCode(), pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 删除
	 */
	@Transactional
	public void delete(long id) {
		SendTaskRecord sendTaskRecord = findOne(id);
		//数据库删除
		sendTaskRecordRepository.save(sendTaskRecord.setIsDelete(IntToBoolEnum.TRUE.getCode()));

		log.info("[sendTaskRecord]删除任务.sendTaskRecord:{}", sendTaskRecord);
		//如果任务在等待中状态,从任务队列删除
		if (EnumUtil.equals(sendTaskRecord.getStatus(), SendTaskRecordStatusEnum.WAIT)) {
			log.info("[sendTaskRecord]删除任务.将任务从等待队列中删除中.");
			//任务等待队列删除
			sendTaskManager.removeTask(id);
		}
		//如果任务在运行中,中断任务
		if (EnumUtil.equals(sendTaskRecord.getStatus(), SendTaskRecordStatusEnum.RUN)) {
			log.info("[sendTaskRecord]删除任务.任务运行中,正在停止它.");
			stop(id);
		}


		log.info("[sendTaskRecord]删除任务.成功.");
	}

	/**
	 * 修改任务
	 */
	@Transactional
	public void updateInfo(SendTaskRecordUpdateForm form) {
		//查询
		SendTaskRecord sendTaskRecord = findOne(form.getId());
		if(!EnumUtil.equals(sendTaskRecord.getStatus(),SendTaskRecordStatusEnum.WAIT)){
			throw new SmsSenderException("任务未在等待状态,无法修改");
		}

		//修改新对象
		BeanUtils.copyProperties(form, sendTaskRecord);
		//数据库保存对象
		sendTaskRecordRepository.save(sendTaskRecord);
		//在队列中修改任务
		sendTaskManager.modifyTask(sendTaskRecord);
	}

	/**
	 * 手动停止任务
	 */
	public void stop(Long id) {
		SendTaskRecord sendTaskRecord = findOne(id);
		if(!EnumUtil.equals(sendTaskRecord.getStatus(),SendTaskRecordStatusEnum.RUN))
			throw new SmsSenderException("任务不在运行中,无法中断");

		sendTaskManager.interruptTask(id);
	}


	/**
	 * 新建任务
	 */
	@Transactional
	public void addTask(SendTaskRecordAddForm form) {
		//新建
		SendTaskRecord sendTaskRecord = new SendTaskRecord();
		//拷贝属性
		BeanUtils.copyProperties(form, sendTaskRecord);

		//设置
		//号码组
		NumberGroup numberGroup = numberGroupService.findOne(form.getNumberGroupId());
		//话术ids
		List<SmsContent> smsContents = smsContentService.findAllById(form.getSmsContentId());
		//全部记录用于一次性加入任务队列
		final List<SendTaskRecord> recordList = new ArrayList<>();
		//遍历话术,创建若干任务
		for (int i = 0; i < smsContents.size(); i++) {
			SmsContent smsContent = smsContents.get(i);
			sendTaskRecord.setChannelName(channelStore.get(form.getChannelId()).getName())//通道名
					.setNumberGroupName(numberGroup.getName())//号码组名
					.setSmsContentId(smsContent.getId())//话术id
					.setSmsContentName(smsContent.getName())//话术名
					.setContent(smsContent.getContent())//话术内容
					.setNumberNum(numberGroup.getNumberCount())//号码数
					.setName(smsContents.size() == 1 ? form.getName() : (form.getName() + "("+ (i+1) +")"))//名字
			;
			//保存到数据库
			SendTaskRecord resultSendTaskRecord = null;
			try {
				resultSendTaskRecord = sendTaskRecordRepository.save((SendTaskRecord)sendTaskRecord.clone());
				recordList.add(resultSendTaskRecord);
			} catch (CloneNotSupportedException e) {
				log.error("[发送任务记录]sendTaskRecord拷贝失败.e:{}",e.getMessage(),e);
				throw new SmsSenderException("sendTaskRecord拷贝失败");
			}
		}

		//异步加入任务
		asyncAddTaskManager(recordList);
	}


	//系统内部调用

	/**
	 * 将若干任务添加到任务管理器
	 */
	private void asyncAddTaskManager(final Collection<SendTaskRecord> tasks) {
		if(CollectionUtils.isEmpty(tasks))
			return;
		/**
		 * 此处如果直接将任务加入等待队列,一旦任务是可以直接开始的.
		 * 就会导致等待任务线程直接从等待任务队列中拉取出任务,执行任务开始.
		 * 在任务准备时.需要根据id再次查询任务,就会导致这个方法的事务还未提交,那边查询不到
		 *
		 * 此bug一般在 多个任务提交时,第一个入队的任务 会发生
		 */
		new Thread(new Runnable() {
			@Override
			public void run() {
				Object o = new Object();
				synchronized (o){
					try {
						o.wait(1000);
					} catch (InterruptedException e) {
						log.error("[新增发送任务]等待失败?!!!");
					}
				}

				for (SendTaskRecord item : tasks) {
					//加入到发送任务管理器
					sendTaskManager.openTask(item);
				}
			}
		}).start();
	}


	/**
	 * 修改记录状态
	 */
	@Transactional
	public SendTaskRecord updateStatus(Long id, SendTaskRecordStatusEnum statusEnum) {
		return updateStatus(id, statusEnum, null);
	}

	/**
	 * 修改记录状态 CAS
	 * 主要是为了防止任务运行时,中断被暂停的任务时,暂停任务启动后,将状态更改回运行状态
	 */
	@Transactional
	public SendTaskRecord updateStatusCAS(Long id, SendTaskRecordStatusEnum oldStatusEnum, SendTaskRecordStatusEnum newStatusEnum) {
		SendTaskRecord sendTaskRecord = sendTaskRecordRepository.findOne(id);
		if(!EnumUtil.equals(sendTaskRecord.getStatus(),oldStatusEnum))
			return sendTaskRecord;
		sendTaskRecord.setStatus(newStatusEnum.getCode());
		return sendTaskRecordRepository.save(sendTaskRecord);
	}

	/**
	 * 修改记录状态,以及异常信息
	 */
	@Transactional
	public SendTaskRecord updateStatus(long id, SendTaskRecordStatusEnum statusEnum, String errorInfo) {
		SendTaskRecord sendTaskRecord = sendTaskRecordRepository.findOne(id);
		sendTaskRecord.setStatus(statusEnum.getCode());
		if (StringUtils.isNotBlank(errorInfo))
			sendTaskRecord.setErrorInfo(sendTaskRecord.getErrorInfo() + "|" + errorInfo);
		return sendTaskRecordRepository.save(sendTaskRecord);
	}

	/**
	 * 累加记录异常信息
	 */
	@Transactional
	public SendTaskRecord updateErrorInfo(long id,String errorInfo) {
		SendTaskRecord record = findOne(id);
		record.setErrorInfo(record.getErrorInfo() + "|" + errorInfo);
		return save(record);
	}

	/**
	 * 保存
	 */
	public SendTaskRecord save(SendTaskRecord sendTaskRecord) {
		return sendTaskRecordRepository.save(sendTaskRecord);
	}


	/**
	 * 定时
	 * 查询所有还在运行中/暂停/等待的任务,如果当前时间大于结束时间超过一定值,修改任务状态
	 */
	public void cleanFailedTask() {
		List<SendTaskRecord> result = sendTaskRecordRepository.findAllByStatusIn(
				new Integer[]{SendTaskRecordStatusEnum.RUN.getCode(),
						SendTaskRecordStatusEnum.PAUSE.getCode(),
					SendTaskRecordStatusEnum.WAIT.getCode()});
		for (SendTaskRecord item : result) {
			//当前时间 - 结束时间
			long l = System.currentTimeMillis() - item.getExpectEndTime().getTime();
			//如果超过结束时间30分钟
			if(l > 0 && TimeUnit.MINUTES.convert(l,TimeUnit.MILLISECONDS) > 30)
				item.setStatus(SendTaskRecordStatusEnum.FAILED.getCode())
						.setErrorInfo("任务异常");
		}
		sendTaskRecordRepository.save(result);
	}

	/**
	 * 启动时调用,将所有还是等待中的任务加入
	 */
	public void addTaskWhenStart() {
		List<SendTaskRecord> result = sendTaskRecordRepository.findAllByStatusIn(new Integer[]{SendTaskRecordStatusEnum.WAIT.getCode()});
		if(CollectionUtils.isEmpty(result))
			return;
		//要增加到任务队列中的任务
		List<SendTaskRecord> addToQueue = new ArrayList<>();

		for (SendTaskRecord item : result) {
			//如果当前时间大于任务结束时间
			if (new Date().after(item.getExpectEndTime())) {
				item.setStatus(SendTaskRecordStatusEnum.FAILED.getCode())
						.setErrorInfo("任务从未启动异常");
				sendTaskRecordRepository.save(item);
			}
			//否则将任务加入等待队列
			else{
				addToQueue.add(item);
			}
		}
		asyncAddTaskManager(addToQueue);
	}

}
