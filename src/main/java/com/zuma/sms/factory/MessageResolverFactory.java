package com.zuma.sms.factory;

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
@Deprecated
public class MessageResolverFactory {

	public static MessageResolver build(Channel channel) {

//		//如果是创蓝
//		if(EnumUtil.equals(channel.getKeyName(), ChannelEnum.CHUANGLAN_YD)) {
//			return new MessageResolver() {
//				@Override
//				public PhoneMessagePair resolve(String phone, String message) {
//
////					//{phone:halfshow}替换为变量
////					boolean flag1 = false;
////					int index1;
////					if((index1 = message.indexOf("{phone:halfshow}")) != -1){
////						flag1 = true;
////						message = resolveHalfPhone(message,"${var}");
////					}
////					//{phone}替换为变量
////					boolean flag2 = false;
////					int index2;
////					//如果该变量被包含在其中
////					if(message.matches("^.*\\{url:.*\\{phone}(.*|.*\\{.*}.*)}.*$"))
////
////					//{url:}替换为变量
////					boolean flag3 = false;
////					int index3;
////					if ((index3 = message.indexOf("{url:")) != -1) {
////						flag3 = true;
////						message = resolveShortUrl(message);
////
////					}
//
//
//					return new PhoneMessagePair(phone,message);
//				}
//			};
//		}
		return null;
	}



	public static void main(String[] args) {
//		MessageResolver xxx = build(new Channel().setName("xxx"));
//		PhoneMessagePair resolve = xxx.resolve("17826824998",
//				"尊敬的{phone:halfshow}: 您的12月福利已到账，点击领取{url:https://tianyiring.com/m/pop/18&02340710130052.html?a={phone}} 回N不收此短信【翼铃】");
//		System.out.println(resolve.getMessage());

	}
}
