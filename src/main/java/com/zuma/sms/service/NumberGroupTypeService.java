package com.zuma.sms.service;

import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.NumberGroupTypeForm;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.NumberGroupType;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.repository.NumberGroupRepository;
import com.zuma.sms.repository.NumberGroupTypeRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 16:01
 * 号码组类别
 */
@Service
public class NumberGroupTypeService {
	@Autowired
	private NumberGroupTypeRepository numberGroupTypeRepository;
	@Autowired
	private NumberGroupRepository numberGroupRepository;
	@Autowired
	private ConfigStore configStore;
	@Autowired
	private PageRequestFactory pageRequestFactory;

	/**
	 * 根据名字模糊查询
	 */
	public PageVO<NumberGroupType> searchByName(String name) {
		Page<NumberGroupType> page = numberGroupTypeRepository.findByNameContaining(name, pageRequestFactory.buildForLikeSearch());
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 查询单个
	 */
	public NumberGroupType findOne(Long id) {
		NumberGroupType numberGroupType = numberGroupTypeRepository.findOne(id);
		if (numberGroupType == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return numberGroupType;
	}

	/**
	 * 分页查询
	 */
	public PageVO<NumberGroupType> findPage(Pageable pageable) {
		Page<NumberGroupType> page = numberGroupTypeRepository.findAll(pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 批量删除
	 */
	public void batchDelete(Long[] ids){
		Page<NumberGroup> page = numberGroupRepository.findByTypeIdIn(ids, pageRequestFactory.buildForLimitOne());
		if (page.getTotalElements() > 0)
			throw new SmsSenderException("类别下存在号码组,无法删除");
		numberGroupTypeRepository.deleteAllByIdIn(ids);
	}

	/**
	 * 新增
	 */
	public void save(NumberGroupTypeForm form){
		NumberGroupType numberGroupType = new NumberGroupType();
		BeanUtils.copyProperties(form,numberGroupType);

		numberGroupTypeRepository.save(numberGroupType);
	}

	/**
	 * 修改
	 */
	@Transactional
	public void update(NumberGroupTypeForm form){
		NumberGroupType numberGroupType = numberGroupTypeRepository.findOne(form.getId());
		BeanUtils.copyProperties(form,numberGroupType);

		//修改关联号码组的号码组类别名信息
		numberGroupRepository.updateTypeNameByTypeId(form.getName(),form.getId());

		numberGroupTypeRepository.save(numberGroupType);
	}

	/**
	 * 查询所有
	 */
	public List<NumberGroupType> listAll() {
		return numberGroupTypeRepository.findAll(pageRequestFactory.buildSort());
	}




}
