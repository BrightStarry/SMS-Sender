package com.zuma.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 14:25
 * 手机号和消息对 用于任务发送时的发送消息解析返回
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneMessagePair {
	private String phones;
	private String Message;

	public PhoneMessagePair(String phones) {
		this.phones = phones;
	}
}
