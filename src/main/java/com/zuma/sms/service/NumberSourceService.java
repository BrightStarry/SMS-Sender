package com.zuma.sms.service;

import com.sun.xml.internal.fastinfoset.Encoder;
import com.zuma.sms.enums.db.IntToBoolEnum;
import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.form.NumberSourceForm;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.converter.JPAPage2PageVOConverter;
import com.zuma.sms.dto.PageVO;
import com.zuma.sms.entity.NumberSource;
import com.zuma.sms.enums.db.IsDeleteEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.repository.NumberGroupRepository;
import com.zuma.sms.repository.NumberSourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;

/**
 * author:ZhengXing
 * datetime:2017/12/7 0007 12:53
 * 号码源
 */
@Service
@Slf4j
public class NumberSourceService {
	@Autowired
	private NumberSourceRepository numberSourceRepository;

	@Autowired
	private NumberGroupRepository numberGroupRepository;

	@Autowired
	private ConfigStore configStore;

	@Autowired
	private PageRequestFactory pageRequestFactory;

	/**
	 * 查询所有
	 */
	public List<NumberSource> listAll(IsDeleteEnum isDeleteEnum) {
		return numberSourceRepository.findAllByIsDelete(isDeleteEnum.getCode());
	}

	/**
	 * 查询单个
	 */
	public NumberSource findOne(Long id) {
		NumberSource numberSource = numberSourceRepository.findOne(id);
		if (numberSource == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return numberSource;
	}

	/**
	 * 修改
	 */
	@Transactional
	public void updateOne(NumberSourceForm form) {
		NumberSource real = numberSourceRepository.findOne(form.getId());
		BeanUtils.copyProperties(form,real);

		//修改关联号码组中的冗余字段
		//修改该号码源下的号码组的号码源名字
		numberGroupRepository.updateNumberSourceNameByNumberSourceId(form.getName(),form.getId());

		numberSourceRepository.save(real);
	}

	/**
	 * 根据名字模糊查询
	 */
	public PageVO<NumberSource> searchByName(String name, IsDeleteEnum isDeleteEnum) {
		Pageable pageable = pageRequestFactory.buildForLikeSearch();
		Page<NumberSource> page = numberSourceRepository.findByNameContainingAndIsDeleteEquals(name,isDeleteEnum.getCode(), pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 批量删除
	 */
	@Transactional
	public void batchDelete(Long[] ids){
		List<NumberSource> list = numberSourceRepository.findAllByIdIn(ids);
		if (CollectionUtils.isEmpty(list))
			return;
		for (NumberSource item : list) {
			item.setIsDelete(IsDeleteEnum.DELETED.getCode());
			if(item.getStatus().equals(IntToBoolEnum.TRUE.getCode()))
				throw new SmsSenderException("id:" + item.getId() + " 已分组,无法删除.");
		}
		numberSourceRepository.save(list);
	}


	/**
	 * 分页查询所有数据源.
	 */
	public PageVO<NumberSource> findPage(Pageable pageable, IsDeleteEnum isDeleteEnum) {
		Page<NumberSource> page = numberSourceRepository.findAllByIsDelete(isDeleteEnum.getCode(), pageable);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 新增数据源记录 此处一次性写入
	 */
	@Transactional
	public void add(List<MultipartFile> files, String[] names, String[] remarks) {
			for (int i = 0; i < files.size(); i++) {
				//文件
				MultipartFile file = files.get(i);
				//手机号字符
				String tmp;
				try {
					tmp = IOUtils.toString(file.getInputStream(), "UTF-8");
				} catch (IOException e) {
					log.error("[号码源]文件读取异常.e:{}",e.getMessage(),e);
					throw new SmsSenderException(ErrorEnum.IO_ERROR);
				}
				//切割
				String[] phones = StringUtils.split(tmp, ",");
				//并计算长度
				int length = phones.length;
				if (length == 0)
					throw new SmsSenderException(ErrorEnum.PHONE_EMPTY);
				for (int j = 0; j < phones.length; j++) {
					if (!StringUtils.isNumeric(phones[j]) || phones[j].length() != 11)
						throw new SmsSenderException(ErrorEnum.NUMBER_SOURCE_PHONE_FORMAT_ERROR);
				}
				//新建记录存储.
				NumberSource numberSource = numberSourceRepository.save(new NumberSource(names[i], remarks[i], length));
				//写入文件
				try {
					FileUtils.writeStringToFile(getFile(numberSource.getId()),StringUtils.join(phones,","), Encoder.UTF_8);
				} catch (IOException e) {
					log.error("[号码源]文件写入异常.e:{}",e.getMessage(),e);
					throw new SmsSenderException(ErrorEnum.IO_ERROR);
				}
			}
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
		return new File(configStore.numberSourcePre + id + ".txt");
	}

}
