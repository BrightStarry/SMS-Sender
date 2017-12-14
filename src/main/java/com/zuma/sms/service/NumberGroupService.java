package com.zuma.sms.service;

import com.google.common.base.Utf8;
import com.sun.xml.internal.fastinfoset.Encoder;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.entity.NumberGroupType;
import com.zuma.sms.entity.NumberSource;
import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.enums.db.NumberGroupModeEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.NumberGroupAddForm;
import com.zuma.sms.form.NumberGroupUpdateForm;
import com.zuma.sms.repository.NumberGroupRepository;
import com.zuma.sms.repository.NumberSourceRepository;
import com.zuma.sms.repository.SendTaskRecordRepository;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:47
 * 号码组
 */
@Service
@Slf4j
public class NumberGroupService {

	@Autowired
	private NumberGroupRepository numberGroupRepository;

	@Autowired
	private NumberSourceService numberSourceService;

	@Autowired
	private PageRequestFactory pageRequestFactory;

	@Autowired
	private NumberGroupTypeService numberGroupTypeService;

	@Autowired
	private NumberSourceRepository numberSourceRepository;

	@Autowired
	private SendTaskRecordRepository sendTaskRecordRepository;

	@Autowired
	private ConfigStore configStore;


	/**
	 * 查询某类别下所有号码组
	 */
	public List<NumberGroup> findByType(Long typeId) {
		return numberGroupRepository.findAllByTypeIdEquals(typeId,pageRequestFactory.buildSort());
	}

	/**
	 * 查询所有
	 */
	public Map<String, List<NumberGroup>> listAllGroup() {
		Map<String, List<NumberGroup>> result = new HashMap<>();
		//查询所有类别
		List<NumberGroupType> numberGroupTypes = numberGroupTypeService.listAll();
		for (NumberGroupType item : numberGroupTypes) {
			List<NumberGroup> numberGroups = findByType(item.getId());
			result.put(item.getName(), numberGroups);
		}
		return result;
	}

	/**
	 * 查询所有
	 */
	public List<NumberGroup> listAll() {
		return numberGroupRepository.findAll();
	}

	/**
	 * 根据名字模糊查询
	 */
	public PageVO<NumberGroup> searchByName(String name) {
		Page<NumberGroup> page = numberGroupRepository.findByNameContaining(name, pageRequestFactory.buildForLikeSearch());
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 查询单个
	 */
	public NumberGroup findOne(Long id) {
		NumberGroup numberGroup = numberGroupRepository.findOne(id);
		if (numberGroup == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return numberGroup;
	}

	/**
	 * 分页查询
	 */
	public PageVO<NumberGroup> findPage(Pageable pageable) {
		Page<NumberGroup> page = numberGroupRepository.findAll(pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 批量删除
	 */
	public void batchDelete(Long[] ids) {
		Page<SendTaskRecord> page = sendTaskRecordRepository.findByNumberGroupIdIn(ids, pageRequestFactory.buildForLimitOne());
		if (page.getTotalElements() > 0) {
			throw new SmsSenderException("号码组已使用,无法操作");
		}
		numberGroupRepository.deleteAllByIdIn(ids);
	}

	/**
	 * 新增
	 */
	@Transactional
	public void save(NumberGroupAddForm form) {
		//验证 组类别是否存在
		NumberGroupType numberGroupType = numberGroupTypeService.findOne(form.getTypeId());
		//除了手动分组,其余模式 号码总数必须指定
		if (!EnumUtil.equals(form.getGroupMode(), NumberGroupModeEnum.MANUAL_MODE) && form.getNumberCount() == null) {
			log.error("[号码组服务]非手动分组模式时,号码总数未指定");
			throw new SmsSenderException("号码总数未指定");
		}
		//号码源
		NumberSource numberSource = numberSourceService.findOne(form.getNumberSourceId());
		//除了手动分组,其余模式 号码总数小于等于号码源号码总数
		if (!EnumUtil.equals(form.getGroupMode(), NumberGroupModeEnum.MANUAL_MODE)
				&& form.getNumberCount() > numberSource.getNumberCount()) {
			log.error("[号码组服务]非手动分组模式时,号码总数大于号码源号码数");
			throw new SmsSenderException("号码总数过大");
		}
		//-----------椒盐结束



		//号码数组
		String[] numberArr = null;

		//根据模式选择不同方法
		switch (EnumUtil.getByCode(form.getGroupMode(), NumberGroupModeEnum.class,
				"[numberGroup]新增异常.分组模式不存在.currentMode:{}", form.getGroupMode())) {
			//顺序分组
			case SEQUENCE_MODE:
				//获取 号码数量相同的号码字符, ","分割
				try (BufferedInputStream inputStream = numberSourceService.getInputStream(numberSource.getId())) {
					String tmp = IOUtils.toString(inputStream, Encoder.UTF_8);
					String[] numberSplit = StringUtils.split(tmp,",");
					numberArr = ArrayUtils.subarray(numberSplit, 0, form.getNumberCount());
				} catch (IOException e) {
					log.error("[numberSource]新增/顺序分组模式,从文件中读取指定数目号码失败.e:{}",e.getMessage(), e);
					throw new SmsSenderException(ErrorEnum.IO_ERROR);
				}
				break;
			//随机分组
			case RANDOM_MODE:
				//获取 号码数量相同的号码字符, ","分割
				try (BufferedInputStream inputStream = numberSourceService.getInputStream(numberSource.getId())) {
					String tmp = IOUtils.toString(inputStream, Encoder.UTF_8);
					numberArr = StringUtils.split(tmp,",");
					//使用list
					List<String> numberList = new ArrayList<>(Arrays.asList(numberArr));
					//只需增加的结果使用linkedList
					List<String> resultList = new LinkedList<>();

					//判断是使用删除元素的方法取,还是获取元素的方法取
					//如果差大,表示要用获取元素的方法取
					int len = numberArr.length;
					int deleteCount = len - form.getNumberCount();
					int numberCount = form.getNumberCount();
					if( deleteCount > numberCount){
						for (int i = 0; i < numberCount; i++) {
							resultList.add(numberList.remove(RandomUtils.nextInt(0, len - i)));
						}
					}else{
						//否则用删减方式获取
						for (int i = 0; i < deleteCount; i++) {
							numberList.remove(RandomUtils.nextInt(0, len - i));
						}
					}

					numberArr = deleteCount > numberCount ?
							resultList.toArray(new String[numberCount]) :
							numberList.toArray(new String[numberCount]);
				} catch (IOException e) {
					log.error("[numberSource]新增/随机分组模式,从文件中读取指定数目号码失败.e:{}",e.getMessage(), e);
					throw new SmsSenderException(ErrorEnum.IO_ERROR);
				}
				break;
			//手动分组
			case MANUAL_MODE:
				numberArr = StringUtils.split(form.getPhones(),",");
				form.setNumberCount(numberArr.length);
				if(ArrayUtils.isEmpty(numberArr)){
					log.error("[numberSource]新增/手动分组模式,号码字符为空");
					throw new SmsSenderException(ErrorEnum.PHONE_EMPTY);
				}
				break;
		}
		//在数据库新增记录
		NumberGroup numberGroup = new NumberGroup();
		BeanUtils.copyProperties(form, numberGroup);
		numberGroup.setNumberSourceName(numberSource.getName()).setTypeName(numberGroupType.getName());
		numberGroup = numberGroupRepository.save(numberGroup);

		//修改号码源信息
		numberSource.setStatus(IntToBoolEnum.TRUE.getCode());//已分组
		numberSource.setGroupedCount(numberGroup.getNumberCount());//已分组号码数
		numberSourceRepository.save(numberSource);//保存

		//拼接号码数组,产生最终结果
		String result = StringUtils.join(numberArr, ",");
		try {
			FileUtils.writeStringToFile(getFile(numberGroup.getId()),result, Encoder.UTF_8);
		} catch (IOException e) {
			log.error("[numberSource]新增,写入字符到号码文件失败.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.IO_ERROR);
		}
	}

	/**
	 * 修改
	 */
	@Transactional
	public void update(NumberGroupUpdateForm form) {
		NumberGroup numberGroup = numberGroupRepository.findOne(form.getId());
		BeanUtils.copyProperties(form, numberGroup);

		//查询号码组类别
		NumberGroupType numberGroupType = numberGroupTypeService.findOne(form.getTypeId());
		numberGroup.setTypeName(numberGroupType.getName());

		//修改发送任务中关联记录的号码组名
		sendTaskRecordRepository.updateNumberGroupNameByNumberGroupId(form.getName(), form.getId());

		numberGroupRepository.save(numberGroup);
	}

	/**
	 * 根据id获取号码文件输入流
	 * 需要自行校验id是否存在
	 */
	public BufferedInputStream getInputStream(Long id) {
		try {
			return new BufferedInputStream(new FileInputStream(getFile(id)));
		} catch (FileNotFoundException e) {
			log.error("[numberSource]号码源号码文件不存在.id:{}",id);
			throw new SmsSenderException(ErrorEnum.NUMBER_SOURCE_FILE_NOT_EXIST);
		}
	}

	/**
	 * 根据id获取号码文件file
	 */
	public File getFile(Long id) {
		return new File(configStore.numberGroupPre + id + ".txt");
	}
}
