package com.zuma.sms.enums.db;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/7 0007 17:04
 * 数字对应的布尔值
 */
@Getter
public enum IntToBoolEnum implements CodeEnum<Integer> {
	FALSE(0, "否"),
	TRUE(1, "是"),
	;

	private Integer code;
	private String message;

	IntToBoolEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
