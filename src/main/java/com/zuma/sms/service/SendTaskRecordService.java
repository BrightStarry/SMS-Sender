package com.zuma.sms.service;

import com.zuma.sms.entity.SendTaskRecord;
import com.zuma.sms.enums.db.SendTaskRecordStatusEnum;
import com.zuma.sms.repository.SendTaskRecordRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 12:19
 * 发送任务记录
 */
@Service
@Slf4j
public class SendTaskRecordService {
	@Autowired
	private SendTaskRecordRepository sendTaskRecordRepository;

	/**
	 * 修改记录状态
	 */
	@Transactional
	public void updateStatus(Long id, SendTaskRecordStatusEnum statusEnum){
		updateStatus(id,statusEnum,null);
	}

	/**
	 * 修改记录状态,以及异常信息
	 */
	@Transactional
	public void updateStatus(Long id, SendTaskRecordStatusEnum statusEnum, String errorInfo){
		SendTaskRecord sendTaskRecord = sendTaskRecordRepository.findOne(id);
		sendTaskRecord.setStatus(statusEnum.getCode());
		if(StringUtils.isNotBlank(errorInfo))
			sendTaskRecord.setErrorInfo(errorInfo);
		sendTaskRecordRepository.save(sendTaskRecord);
	}

	/**
	 * 保存
	 */
	public void updateOne(SendTaskRecord sendTaskRecord){
		sendTaskRecordRepository.save(sendTaskRecord);

	}
}
