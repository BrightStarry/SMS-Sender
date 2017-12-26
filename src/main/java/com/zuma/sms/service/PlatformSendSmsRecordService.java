package com.zuma.sms.service;

import com.zuma.sms.entity.PlatformSendSmsRecord;
import com.zuma.sms.enums.db.PlatformSendSmsRecordStatusEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.repository.PlatformSendSmsRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 09:52
 * 平台调用发送短信记录
 */
@Service
@Slf4j
public class PlatformSendSmsRecordService {

	@Autowired
	private PlatformSendSmsRecordRepository platformSendSmsRecordRepository;

	/**
	 * 保存
	 */
	public PlatformSendSmsRecord save(PlatformSendSmsRecord platformSendSmsRecord) {
		return platformSendSmsRecordRepository.save(platformSendSmsRecord);
	}

	/**
	 * 查询单个
	 */
	public PlatformSendSmsRecord findOne(Long id) {
		PlatformSendSmsRecord record = platformSendSmsRecordRepository.findOne(id);
		if(record == null)
			throw new SmsSenderException(ErrorEnum.OBJECT_EMPTY);
		return record;
	}

	/**
	 * 修改 状态 和 返回结果
	 */
	public PlatformSendSmsRecord modifyStatusAndResult(Long id,PlatformSendSmsRecordStatusEnum statusEnum, String result) {
		PlatformSendSmsRecord record = platformSendSmsRecordRepository.findOne(id);
		record.setStatus(statusEnum.getCode()).setResult(result);
		return save(record);
	}
}
