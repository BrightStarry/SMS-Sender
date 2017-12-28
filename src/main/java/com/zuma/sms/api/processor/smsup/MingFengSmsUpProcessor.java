package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.MingFengAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 11:11
 * 铭锋 短信上行 处理
 */
@Component
public class MingFengSmsUpProcessor extends SmsUpProcessor<MingFengAPI.SmsUpResponseChild> {

	@Override
	protected SmsUpRecord responseToSmsUpRecord(MingFengAPI.SmsUpResponseChild response, Channel channel) {
		return new SmsUpRecord(channel.getId(),channel.getName(),response.getMobile(),response.getContent(),
				CodeUtil.objectToJsonString(response), DateUtil.stringToDate(response.getReceivetime(),DateUtil.FORMAT_D));
	}
}
