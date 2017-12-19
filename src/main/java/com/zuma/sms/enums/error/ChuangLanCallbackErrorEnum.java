package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 13:16
 */
@Getter
public enum ChuangLanCallbackErrorEnum implements CodeEnum<String>{


	SUCCESS("DELIVRD","短信发送成功"),
	Z("UNKNOWN","未知短信状态"),
	A1("REJECTD","短信被短信中心拒绝"),
	A2("MBBLACK","目的号码是黑名单号码"),
	A3("REJECT"	,"审核驳回"),
	;

	private String code;
	private String message;

	ChuangLanCallbackErrorEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
