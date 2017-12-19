package com.zuma.sms.api.resolver;

import com.zuma.sms.PhoneMessagePair;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 14:28
 * 短信消息解析- 创蓝的特殊情况,可能还要解析手机,所以返回 手机 和消息
 */
public interface MessageResolver {
	PhoneMessagePair resolve(String phone, String message);
}
