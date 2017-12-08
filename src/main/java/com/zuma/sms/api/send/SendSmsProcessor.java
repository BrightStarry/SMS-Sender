package com.zuma.sms.api.send;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 16:57
 * 发送短信处理器
 */
public interface SendSmsProcessor {

	/**
	 * 处理方法,包装下
	 */
	ResultDTO<ErrorData> process(Channel channel, String phones, String message, Long taskId);

	/**
	 * 处理方法,包装下
	 */
	ResultDTO<ErrorData> process(Channel channel, String phones, String message, Platform platform);
}
