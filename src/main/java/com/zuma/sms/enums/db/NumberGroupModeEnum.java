package com.zuma.sms.enums.db;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/8 0008 17:36
 * 号码组模式枚举
 */
@Getter
public enum NumberGroupModeEnum implements CodeEnum<Integer> {

	SEQUENCE_MODE(0, "顺序"),
	RANDOM_MODE(1, "随机"),
	MANUAL_MODE(2, "手动"),
	;
	private Integer code;
	private String message;

	NumberGroupModeEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
