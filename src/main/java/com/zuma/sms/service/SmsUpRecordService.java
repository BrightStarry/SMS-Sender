package com.zuma.sms.service;

import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.repository.SmsUpRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 10:01
 * 短信上行记录
 */
@Service
public class SmsUpRecordService {

	@Autowired
	private SmsUpRecordRepository smsUpRecordRepository;

	/**
	 * 保存
	 */
	public SmsUpRecord save(SmsUpRecord record) {
		return smsUpRecordRepository.save(record);
	}
}
