package com.zuma.sms.service;

import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.SmsContentForm;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.SmsContent;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.repository.SendTaskRecordRepository;
import com.zuma.sms.repository.SmsContentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

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
	private SendTaskRecordRepository sendTaskRecordRepository;
	@Autowired
	private ConfigStore configStore;
	@Autowired
	private PageRequestFactory pageRequestFactory;

	/**
	 * 查询所有
	 */
	public List<SmsContent> listAll() {
		return smsContentRepository.findAll();
	}


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
		//校验话术是否使用过
		verifyIsUsedByIds(ids);

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
		//校验话术是否使用过
		verifyIsUsedByIds(new Long[]{smsContentForm.getId()});

		SmsContent smsContent = smsContentRepository.findOne(smsContentForm.getId());
		BeanUtils.copyProperties(smsContentForm,smsContent);
		/**
		 * verify
		 */

		smsContentRepository.save(smsContent);
	}


	/**
	 * 根据id查询所有
	 */
	public List<SmsContent> findAllById(Long[] ids) {
		return smsContentRepository.findAll(Arrays.asList(ids));
	}

	/**
	 * 查询某些话术是否被使用过
	 */
	private void verifyIsUsedByIds(Long[] ids) {
		//查询该话术是否被使用过
		Page<SendTaskRecord> page = sendTaskRecordRepository.findBySmsContentIdIn(
				ids,pageRequestFactory.buildForLimitOne());
		if(page.getTotalElements() > 0)
			throw new SmsSenderException("存在被使用了的话术,无法操作");
	}


}
