package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.ChuangLanAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 11:11
 * 创蓝 短信上行 处理
 */
@Component
public class ChuangLanSmsUpProcessor extends SmsUpProcessor<ChuangLanAPI.SmsUpResponse> {

	@Override
	protected SmsUpRecord responseToSmsUpRecord(ChuangLanAPI.SmsUpResponse response, Channel channel) {
		return new SmsUpRecord(channel.getId(),channel.getName(),response.getMobile(),response.getMsg(),
				CodeUtil.objectToJsonString(response),response.getMoTime());
	}
}
