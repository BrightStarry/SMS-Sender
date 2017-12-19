package com.zuma.sms.api.processor;

import com.zuma.sms.api.processor.callback.SendSmsCallbackProcessor;
import com.zuma.sms.api.processor.send.SendSmsProcessor;
import com.zuma.sms.api.processor.smsup.SmsUpProcessor;
import com.zuma.sms.entity.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 17:03
 * 自定义的各类处理器工厂
 * 封装了 根据channel实体匹配对应 Processor实现类的方法
 */
@Component
public class CustomProcessorFactory {
	//发送短信处理器
	private static Map<String,SendSmsProcessor> sendSmsProcessorMap;
	//短信回调处理器
	private static Map<String,SendSmsCallbackProcessor> sendSmsCallbackProcessorMap;
	//短信上行处理器
	private static Map<String, SmsUpProcessor> smsUpProcessorMap;

	@Autowired
	private void init(Map<String,SendSmsProcessor> sendSmsProcessorMap,
					  Map<String,SendSmsCallbackProcessor> sendSmsCallbackProcessorMap,
					  Map<String, SmsUpProcessor> smsUpProcessorMap) {
		CustomProcessorFactory.sendSmsProcessorMap = sendSmsProcessorMap;
		CustomProcessorFactory.sendSmsCallbackProcessorMap = sendSmsCallbackProcessorMap;
		CustomProcessorFactory.smsUpProcessorMap = smsUpProcessorMap;
	}



	//根据通道和名字后缀构建
	public static SendSmsProcessor buildSendSmsProcessor(Channel channel,String nameSuf) {
		return sendSmsProcessorMap.get(channel.getKeyName() + nameSuf);
	}

	//根据通道使用默认后缀构建
	public static SendSmsProcessor buildSendSmsProcessor(Channel channel) {
		return sendSmsProcessorMap.get(channel.getKeyName() + "SendSmsProcessor");
	}

	//根据通道和名字后缀构建
	public static SendSmsCallbackProcessor buildSendSmsCallbackProcessor(Channel channel,String nameSuf) {
		return sendSmsCallbackProcessorMap.get(channel.getKeyName() + nameSuf);
	}

	//根据通道使用默认后缀构建
	public static SendSmsCallbackProcessor buildSendSmsCallbackProcessor(Channel channel) {
		return sendSmsCallbackProcessorMap.get(channel.getKeyName() + "CallbackProcessor");
	}

	//根据通道和名字后缀构建
	public static SmsUpProcessor buildSmsUpProcessor(Channel channel,String nameSuf) {
		return smsUpProcessorMap.get(channel.getKeyName() + nameSuf);
	}

	//根据通道使用默认后缀构建
	public static SmsUpProcessor buildSmsUpProcessor(Channel channel) {
		return smsUpProcessorMap.get(channel.getKeyName() + "SmsUpProcessor");
	}




}
