package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.ChangXiangAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 11:11
 * 畅想 短信上行 处理
 */
@Component
public class ChangXiangSmsUpProcessor extends SmsUpProcessor<ChangXiangAPI.SmsUpResponse> {

	@Override
	protected SmsUpRecord responseToSmsUpRecord(ChangXiangAPI.SmsUpResponse response, Channel channel) {
		return new SmsUpRecord(channel.getId(),channel.getName(),response.getSrc(),response.getContent(),
				CodeUtil.objectToJsonString(response),response.getTime());
	}
}
