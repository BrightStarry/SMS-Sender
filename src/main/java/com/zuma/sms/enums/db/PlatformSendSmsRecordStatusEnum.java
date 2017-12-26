package com.zuma.sms.enums.db;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/25 0025 09:58
 * 平台调用发送短信记录 状态枚举
 */
@Getter
public enum PlatformSendSmsRecordStatusEnum  implements CodeEnum<Integer> {

	FAILED(-1,"失败"),
	WAIT(0, "等待"),
	SUCCESS(1,"成功")
	;
	private Integer code;
	private String message;

	PlatformSendSmsRecordStatusEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}