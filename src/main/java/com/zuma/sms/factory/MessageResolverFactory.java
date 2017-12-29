package com.zuma.sms.factory;

import com.zuma.sms.api.resolver.MessageResolver;
import com.zuma.sms.dto.PhoneMessagePair;
import com.zuma.sms.entity.Channel;
import com.zuma.sms.enums.system.ChannelEnum;
import com.zuma.sms.util.EnumUtil;
import com.zuma.sms.util.PhoneUtil;
import org.apache.commons.lang3.StringUtils;

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
				//替换手机号
				message = StringUtils.replace(message,"{phone}",phone);
				//替换为**手机号
				message = StringUtils.replace(message,"{phone:halfshow}", PhoneUtil.encryptPhone(phone));
				if(message.contains())


				return  new PhoneMessagePair(phone,message);
			}


		};
	}

	public static void main(String[] args) {
		MessageResolver xxx = build(new Channel().setName("xxx"));
		PhoneMessagePair resolve = xxx.resolve("17826824998",
				"尊敬的{phone:halfshow}: 您的12月福利已到账，点击领取{url:https://tianyiring.com/m/pop/18&02340710130052.html?a={phone}} 回N不收此短信【翼铃】");
		System.out.println(resolve.getMessage());
	}
}
