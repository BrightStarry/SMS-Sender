package com.zuma.sms.service;

import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.entity.NumberGroupType;
import com.zuma.sms.entity.NumberSource;
import com.zuma.sms.enums.db.NumberGroupModeEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.NumberGroupAddForm;
import com.zuma.sms.repository.NumberGroupRepository;
import com.zuma.sms.util.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
	public void batchDelete(Long[] ids){
		numberGroupRepository.deleteAllByIdIn(ids);
	}

	/**
	 * 新增
	 */
	public void save(NumberGroupAddForm form){
		//验证 组类别是否存在
		NumberGroupType numberGroupType = numberGroupTypeService.findOne(form.getTypeId());
		//组类别名称
		if(!StringUtils.equals(numberGroupType.getName(),form.getName())){
			log.error("[号码组服务]组类别名称不一致");
			throw new SmsSenderException(ErrorEnum.RELEVANCE_PARAM_MISMATCHING);
		}
		//除了手动分组,其余模式 号码总数必须指定
		if (!EnumUtil.equals(form.getGroupMode(), NumberGroupModeEnum.MANUAL_MODE) && form.getNumberCount() == null) {
			log.error("[号码组服务]非手动分组模式时,号码总数未指定");
			throw new SmsSenderException(ErrorEnum.FORM_ERROR);
		}
		//号码源
		NumberSource numberSource = numberSourceService.findOne(form.getNumberSourceId());
		//号码源名称
		if (StringUtils.equals(numberSource.getName(), form.getNumberSourceName())) {
			log.error("[号码组服务]号码源名称不一致");
			throw new SmsSenderException(ErrorEnum.FORM_ERROR);
		}
		//除了手动分组,其余模式 号码总数小于等于号码源号码总数
		if (!EnumUtil.equals(form.getGroupMode(), NumberGroupModeEnum.MANUAL_MODE)
				&& form.getNumberCount() > numberSource.getNumberCount()) {
			log.error("[号码组服务]非手动分组模式时,号码总数大于号码源号码数");
			throw new SmsSenderException(ErrorEnum.FORM_ERROR);
		}
		//-----------椒盐结束

		//根据模式选择不同方法
		switch (EnumUtil.getByCode(form.getGroupMode(),NumberGroupModeEnum.class)) {
			case MANUAL_MODE:
				break;

		}

		NumberGroup numberGroup = new NumberGroup();
		BeanUtils.copyProperties(form,numberGroup);

		numberGroupRepository.save(numberGroup);
	}

	/**
	 * 修改
	 */
	public void update(NumberGroupAddForm form){
		NumberGroup numberGroup = numberGroupRepository.findOne(form.getId());
		BeanUtils.copyProperties(form,numberGroup);

		numberGroupRepository.save(numberGroup);
	}
}
