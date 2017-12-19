package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.KuanXinAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 10:10
 * 宽信 短信上行 处理
 */
@Slf4j
@Component
public class KuanXinSmsUpProcessor extends SmsUpProcessor<KuanXinAPI.SmsUpResponse>{


	@Override
	protected SmsUpRecord responseToSmsUpRecord(KuanXinAPI.SmsUpResponse response,Channel channel) {
		return new SmsUpRecord()
				.setChannelId(channel.getId())
				.setChannelName(channel.getName())
				.setPhone(response.getMobile())
				.setContent(response.getMsgContent())
				.setRequestBody(CodeUtil.objectToJsonString(response))
				.setUpTime(DateUtil.stringToDate(response.getTime()));//时间可能为空,如果解析失败
	}
}
