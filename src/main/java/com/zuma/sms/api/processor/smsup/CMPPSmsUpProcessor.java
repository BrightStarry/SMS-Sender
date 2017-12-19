package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.cmpp.CMPPDeliverAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 10:51
 * CMPP 短信上行 处理
 */
@Component
public class CMPPSmsUpProcessor extends SmsUpProcessor<CMPPDeliverAPI.Request> {

	@Override
	protected SmsUpRecord responseToSmsUpRecord(CMPPDeliverAPI.Request response, Channel channel) {
		return new SmsUpRecord()
				.setChannelId(channel.getId())
				.setChannelName(channel.getName())
				.setPhone(response.getSrcTerminalId())
				.setContent(response.getOtherMsgContent())
				.setRequestBody(CodeUtil.objectToJsonString(response))
				.setUpTime(new Date());
	}

}
