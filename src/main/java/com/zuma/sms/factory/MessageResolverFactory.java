package com.zuma.sms.factory;

import com.zuma.sms.api.resolver.CommonMessageResolver;
import com.zuma.sms.api.resolver.MessageResolver;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.util.PhoneUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 14:30
 * 解析器工厂 根据通道返回对应解析器, 目前只有创蓝一个例外
 * 暂时无用,因为只有一个解析器
 */
public class MessageResolverFactory {

	public static MessageResolver build(Channel channel) {
		//返回通用解析器
		return new CommonMessageResolver();
	}




}
