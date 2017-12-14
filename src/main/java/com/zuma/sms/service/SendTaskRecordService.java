package com.zuma.sms.service;

import com.zuma.sms.api.SendTaskManager;
import com.zuma.sms.config.store.ChannelStore;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.entity.NumberSource;
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
import com.zuma.sms.repository.SendTaskRecordRepository;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

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
	public SendTaskRecord findOne(Long id) {
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
	public void delete(Long id) {
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
			throw new SmsSenderException("任务不再运行中,无法停止");

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
			} catch (CloneNotSupportedException e) {
				log.error("[发送任务记录]sendTaskRecord拷贝失败.e:{}",e.getMessage(),e);
				throw new SmsSenderException("sendTaskRecord拷贝失败");
			}

			//加入到发送任务管理器
			sendTaskManager.openTask(resultSendTaskRecord);
		}

	}


	//系统内部调用

	/**
	 * 修改记录状态
	 */
	@Transactional
	public SendTaskRecord updateStatus(Long id, SendTaskRecordStatusEnum statusEnum) {
		return updateStatus(id, statusEnum, null);
	}

	/**
	 * 修改记录状态,以及异常信息
	 */
	@Transactional
	public SendTaskRecord updateStatus(Long id, SendTaskRecordStatusEnum statusEnum, String errorInfo) {
		SendTaskRecord sendTaskRecord = sendTaskRecordRepository.findOne(id);
		sendTaskRecord.setStatus(statusEnum.getCode());
		if (StringUtils.isNotBlank(errorInfo))
			sendTaskRecord.setErrorInfo(errorInfo);
		return sendTaskRecordRepository.save(sendTaskRecord);
	}

	/**
	 * 保存
	 */
	public SendTaskRecord updateOne(SendTaskRecord sendTaskRecord) {
		return sendTaskRecordRepository.save(sendTaskRecord);
	}

}
