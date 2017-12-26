package com.zuma.sms.dto;

import lombok.Data;

/**
 * author:ZhengXing
 * datetime:2017/12/26 0026 15:43
 * 短信发送结果
 */
@Data
public class SendResult {
	//失败的数据
	private ErrorData errorData;
	//发送的手机号数
	private Integer phoneNum;

	public SendResult(ErrorData errorData, Integer phoneNum) {
		this.errorData = errorData;
		this.phoneNum = phoneNum;
	}

	public SendResult(String phones , String message, Integer phoneNum) {
		this.errorData = new ErrorData(phones, message);
		this.phoneNum = phoneNum;
	}
}
