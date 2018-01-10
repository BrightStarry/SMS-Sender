package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.ZhuTongAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 11:11
 * 助通 短信上行 处理
 */
@Component
public class ZhuTongSmsUpProcessor extends SmsUpProcessor<ZhuTongAPI.SmsUpResponse> {

	@Override
	protected SmsUpRecord responseToSmsUpRecord(ZhuTongAPI.SmsUpResponse response, Channel channel) {
		return new SmsUpRecord(channel.getId(),channel.getName(),response.getMobile(),response.getContent(),
				CodeUtil.objectToJsonString(response),new Date());
	}
}
