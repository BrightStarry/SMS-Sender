package com.zuma.sms.enums.db;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 12:25
 * 发送任务记录 状态 枚举 数据库
 */
@Getter
public enum SendTaskRecordStatusEnum implements CodeEnum<Integer> {
	WAIT(0,"等待中"),
	RUN(1,"运行中"),
	PAUSE(3,"暂停中"),
	SUCCESS(2,"成功"),
	FAILED(-1,"失败"),
	INTERRUPT(-2, "中断"),

	;

	private Integer code;
	private String message;

	SendTaskRecordStatusEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
