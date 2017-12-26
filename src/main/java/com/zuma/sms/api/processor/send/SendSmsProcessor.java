package com.zuma.sms.api.processor.send;

import com.zuma.sms.dto.ErrorData;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.dto.SendResult;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.entity.Platform;
import com.zuma.sms.entity.SmsSendRecord;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 16:57
 * 发送短信处理器
 */
public interface SendSmsProcessor {



	/**
	 * 处理方法,包装下
	 */
	ResultDTO<SendResult> process(Channel channel, SmsSendRecord record);


}
