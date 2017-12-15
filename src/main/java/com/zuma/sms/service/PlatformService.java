package com.zuma.sms.service;

import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.entity.NumberGroupType;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.NumberGroupTypeForm;
import com.zuma.sms.form.PlatformAddForm;
import com.zuma.sms.form.PlatformUpdateForm;
import com.zuma.sms.repository.NumberGroupRepository;
import com.zuma.sms.repository.NumberGroupTypeRepository;
import com.zuma.sms.repository.PlatformRepository;
import com.zuma.sms.util.TokenUtil;
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
 * 平台
 */
@Service
public class PlatformService {
	@Autowired
	private PlatformRepository platformRepository;
	@Autowired
	private PageRequestFactory pageRequestFactory;


	/**
	 * 根据名字模糊查询
	 */
	public PageVO<Platform> searchByName(String name) {
		Page<Platform> page = platformRepository.findByNameContaining(name, pageRequestFactory.buildForLikeSearch());
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 查询单个
	 */
	public Platform findOne(Long id) {
		Platform platform = platformRepository.findOne(id);
		if (platform == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return platform;
	}

	/**
	 * 分页查询
	 */
	public PageVO<Platform> findPage(Pageable pageable) {
		Page<Platform> page = platformRepository.findAll(pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 批量删除
	 */
	public void batchDelete(Long[] ids){
		platformRepository.deleteAllByIdIn(ids);
	}

	/**
	 * 新增
	 */
	public void save(PlatformAddForm form){
		Platform platform = new Platform();
		BeanUtils.copyProperties(form,platform);

		//生成令牌
		platform.setToken(TokenUtil.generate());

		platformRepository.save(platform);
	}

	/**
	 * 修改
	 */
	@Transactional
	public void update(PlatformUpdateForm form){
		Platform platform = platformRepository.findOne(form.getId());
		BeanUtils.copyProperties(form,platform);

		if(form.getStatus() == null)
			platform.setStatus(IntToBoolEnum.FALSE.getCode());

		platformRepository.save(platform);
	}

	/**
	 * 查询所有
	 */
	public List<Platform> listAll() {
		return platformRepository.findAll(pageRequestFactory.buildSort());
	}




}
