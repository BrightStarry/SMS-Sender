package com.zuma.sms.api.send;

import com.zuma.sms.entity.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 17:03
 * 短信发送器工厂
 * 封装了 根据channel实体匹配对应 SendSmsProcessor实现类的方法
 */
@Component
public class SendSmsProcessorFactory {
	private static Map<String,SendSmsProcessor> sendSmsProcessorMap;

	// TODO
	@Autowired
	private void init(Map<String,SendSmsProcessor> sendSmsProcessorMap) {
		SendSmsProcessorFactory.sendSmsProcessorMap = sendSmsProcessorMap;
	}

	public static SendSmsProcessor build(Channel channel) {
		return sendSmsProcessorMap.get(channel.getKeyName() + "SendSmsProcessor");
	}

}
