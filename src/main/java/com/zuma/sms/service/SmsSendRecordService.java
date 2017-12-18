package com.zuma.sms.service;

import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SmsSendRecord;
import com.zuma.sms.repository.SmsSendRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 15:37
 * 短信发送记录
 */
@Component
public class SmsSendRecordService {

	@Autowired
	private SmsSendRecordRepository smsSendRecordRepository;

	/**
	 * 新建记录
	 * @param taskId
	 * @param channel
	 * @param phones
	 * @param requestBody
	 * @return
	 */
	public SmsSendRecord newRecord(Platform platform,Long taskId,
								   Channel channel, String phones,  String message,String requestBody) {
		SmsSendRecord temp = new SmsSendRecord(taskId, channel.getId(), channel.getName(), phones, message,requestBody);
		if(platform != null)
			temp.setPlatformId(platform.getId()).setPlatformName(platform.getName());
		return smsSendRecordRepository.save(temp);
	}

	/**
	 * 修改记录
	 */
	public SmsSendRecord save(SmsSendRecord smsSendRecord) {
		return smsSendRecordRepository.save(smsSendRecord);
	}

	/**
	 * 根据otherId查询单挑记录
	 */
	public SmsSendRecord findOneByOtherId(String otherId) {
		return smsSendRecordRepository.findByOtherId(otherId);
	}
}
