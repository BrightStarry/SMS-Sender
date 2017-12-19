package com.zuma.sms.api.processor.smsup;

import com.zuma.sms.dto.api.ZhangYouAPI;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.SmsUpRecord;
import com.zuma.sms.util.CodeUtil;
import com.zuma.sms.util.DateUtil;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 10:45
 * 掌游  短信上行 处理
 */
@Component
public class ZhangYouSmsUpProcessor extends SmsUpProcessor<ZhangYouAPI.AsyncResponse> {

	@Override
	protected SmsUpRecord responseToSmsUpRecord(ZhangYouAPI.AsyncResponse response, Channel channel) {
		//短信消息字段 需要解码
		String msgContent = CodeUtil.base64ToString(CodeUtil.urlEncodeToString(response.getMsgContent()));

		return new SmsUpRecord()
				.setChannelId(channel.getId())
				.setChannelName(channel.getName())
				.setPhone(response.getMobileSource())
				.setContent(msgContent)
				.setRequestBody(CodeUtil.objectToJsonString(response))
				.setUpTime(DateUtil.stringToDate(response.getTimestamp()));//时间可能为空,如果解析失败
	}
}
