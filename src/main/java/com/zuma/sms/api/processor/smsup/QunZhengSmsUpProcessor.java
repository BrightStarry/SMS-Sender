package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.QunZhengAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 10:19
 * 群正 短信上行
 */
@Component
public class QunZhengSmsUpProcessor extends SmsUpProcessor<QunZhengAPI.SmsUpResponseChild> {
	@Override
	protected SmsUpRecord responseToSmsUpRecord(QunZhengAPI.SmsUpResponseChild response, Channel channel) {
		return new SmsUpRecord()
				.setChannelId(channel.getId())
				.setChannelName(channel.getName())
				.setPhone(response.getPhone())
				.setContent(response.getContent())
				.setRequestBody(CodeUtil.objectToJsonString(response))
				.setUpTime(DateUtil.stringToDate(response.getRecvdate(), DateUtil.FORMAT_B));
	}


}
