package com.zuma.sms.enums.db;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/15 0015 16:56
 * 短信发送任务状态枚举
 */
@Getter
public enum SmsSendRecordStatusEnum implements CodeEnum<Integer> {
	DEFAULT(0, "默认"),
	SYNC_SUCCESS(1, "同步成功"),
	ASYNC_SUCCESS(2, "异步成功"),
	SYNC_FAILED(-1,"同步失败"),
	ASYNC_FAILED(-2, "异步失败"),
	;


	private Integer code;
	private String message;

	SmsSendRecordStatusEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
