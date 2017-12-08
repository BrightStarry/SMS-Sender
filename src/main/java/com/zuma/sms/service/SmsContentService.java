package com.zuma.sms.service;

import com.zuma.sms.form.SmsContentForm;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.SmsContent;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.repository.SmsContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 11:17
 * 话术
 */
@Service
@Slf4j
public class SmsContentService {
	@Autowired
	private SmsContentRepository smsContentRepository;
	@Autowired
	private ConfigStore configStore;


	/**
	 * 根据名字模糊查询
	 */
	public PageVO<SmsContent> searchByName(String name) {
		PageRequest pageRequest = new PageRequest(0, configStore.likeSearchMaxNum, new Sort(Sort.Direction.DESC, "id"));
		Page<SmsContent> page = smsContentRepository.findByNameContaining(name, pageRequest);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 查询单个
	 */
	public SmsContent findOne(Long id) {
		SmsContent smsContent = smsContentRepository.findOne(id);
		if (smsContent == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return smsContent;
	}

	/**
	 * 分页查询
	 */
	public PageVO<SmsContent> findPage(Pageable pageable) {
		Page<SmsContent> page = smsContentRepository.findAll(pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 批量删除
	 */
	public void batchDelete(Long[] ids){
		smsContentRepository.deleteAllByIdIn(ids);
	}

	/**
	 * 新增
	 */
	public void save(SmsContentForm smsContentForm){
		SmsContent smsContent = new SmsContent();
		BeanUtils.copyProperties(smsContentForm,smsContent);
		/**
		 * verify TODO
		 */

		smsContentRepository.save(smsContent);
	}

	/**
	 * 修改
	 */
	public void update(SmsContentForm smsContentForm){
		SmsContent smsContent = smsContentRepository.findOne(smsContentForm.getId());
		BeanUtils.copyProperties(smsContentForm,smsContent);
		/**
		 * verify
		 */

		smsContentRepository.save(smsContent);
	}



}
