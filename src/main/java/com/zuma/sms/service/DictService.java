package com.zuma.sms.service;

import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.Dict;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.DictUpdateForm;
import com.zuma.sms.repository.DictRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author:ZhengXing
 * datetime:2017/12/12 0012 13:51
 * 字典表
 */
@Service
@Slf4j
public class DictService {
	@Autowired
	private DictRepository dictRepository;
	@Autowired
	private ConfigStore configStore;
	@Autowired
	private PageRequestFactory pageRequestFactory;

	/**
	 * 根据备注模糊查询
	 */
	public PageVO<Dict> searchByRemark(String remark) {
		Page<Dict> page = dictRepository.findByRemarkContaining(remark, pageRequestFactory.buildForLikeSearch());
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 查询单个
	 */
	public Dict findOne(Long id) {
		Dict dict = dictRepository.findOne(id);
		if (dict == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return dict;
	}

	/**
	 * 分页查询
	 */
	public PageVO<Dict> findPage(Pageable pageable) {
		Page<Dict> page = dictRepository.findAll(pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 修改
	 */
	@Transactional
	public void update(DictUpdateForm form){
		Dict dict = dictRepository.findOne(form.getId());
		BeanUtils.copyProperties(form,dict);

		dict = dictRepository.save(dict);

		configStore.update(dict);
	}




}
