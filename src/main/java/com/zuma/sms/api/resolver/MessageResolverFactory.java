package com.zuma.sms.api.resolver;

import com.zuma.sms.dto.PhoneMessagePair;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.system.ChannelEnum;
import com.zuma.sms.util.EnumUtil;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 14:30
 * 解析器工厂 根据通道返回对应解析器, 目前只有创蓝一个例外
 */
public class MessageResolverFactory {

	//TODO 消息解析器处理
	public static MessageResolver build(Channel channel) {
		//如果是创蓝
		if(EnumUtil.equals(channel.getKeyName(), ChannelEnum.CHUANGLAN_YD)) {
			return new MessageResolver() {
				@Override
				public PhoneMessagePair resolve(String phone, String message) {
					return new PhoneMessagePair(phone,message);
				}
			};
		}
		//其他通道解析器
		return new MessageResolver() {
			@Override
			public PhoneMessagePair resolve(String phone, String message) {
				return  new PhoneMessagePair(phone,message);
			}
		};
	}
}
