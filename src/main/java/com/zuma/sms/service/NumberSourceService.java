package com.zuma.sms.service;

import com.zuma.sms.entity.NumberGroup;
import com.zuma.sms.form.NumberSourceForm;
import com.zuma.sms.config.store.ConfigStore;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

		//修改其他表冗余字段

		//查询所有该号码源下的号码组
		List<NumberGroup> numberGroups = numberGroupRepository.findAllByNumberSourceIdEquals(real.getId());
		//如果名称相同,提出队列
		for (NumberGroup item : numberGroups) {
			if(StringUtils.equals(item.getNumberSourceName(),real.getName()))
				numberGroups.remove(item);
			else
				item.setNumberSourceName(real.getName());
		}
		numberGroupRepository.save(numberGroups);


		numberSourceRepository.save(real);
	}

	/**
	 * 根据名字模糊查询
	 */
	public PageVO<NumberSource> searchByName(String name, IsDeleteEnum isDeleteEnum) {
		PageRequest pageRequest = new PageRequest(0, configStore.likeSearchMaxNum, new Sort(Sort.Direction.DESC, "id"));
		Page<NumberSource> page = numberSourceRepository.findByNameContainingAndIsDeleteEquals(name,isDeleteEnum.getCode(), pageRequest);
		return JPAPage2PageVOConverter.convert(page);
	}

	/**
	 * 批量删除
	 */
	@Transactional
	public void batchDelete(Long[] ids){
		List<NumberSource> list = numberSourceRepository.findAllByIdIn(ids);
		for (NumberSource item : list) {
			item.setIsDelete(IsDeleteEnum.DELETED.getCode());
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
	 * 新增数据源记录
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
					throw new SmsSenderException(ErrorEnum.NUMBER_SOURCE_PHONE_EMPTY);
				for (int j = 0; j < phones.length; j++) {
					if (!StringUtils.isNumeric(phones[j]) || phones[j].length() != 11)
						throw new SmsSenderException(ErrorEnum.NUMBER_SOURCE_PHONE_FORMAT_ERROR);
				}
				//新建记录存储.
				NumberSource numberSource = numberSourceRepository.save(new NumberSource(names[i], remarks[i], length));
				//写入文件
				try {
					FileUtils.copyToFile(file.getInputStream(),
							new File(configStore.numberSourcePre + numberSource.getId() + ".txt"));
				} catch (IOException e) {
					log.error("[号码源]文件写入异常.e:{}",e.getMessage(),e);
					throw new SmsSenderException(ErrorEnum.IO_ERROR);
				}
			}
	}

}
