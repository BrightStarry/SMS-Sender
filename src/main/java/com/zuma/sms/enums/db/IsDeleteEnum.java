package com.zuma.sms.enums.db;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/7 0007 13:10
 */
@Getter
public enum IsDeleteEnum implements CodeEnum<Integer> {
	DELETED(1,"被删除的"),
	NOT_DELETE(0,"未删除的"),

	;
	private Integer code;
	private String message;

	IsDeleteEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
