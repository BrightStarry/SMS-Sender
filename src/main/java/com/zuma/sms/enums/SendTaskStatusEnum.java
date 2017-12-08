package com.zuma.sms.enums;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/6 0006 09:22
 * 发送任务 状态
 * See{@link com.zuma.sms.api.SendTask}
 */
@Getter
public enum SendTaskStatusEnum implements CodeEnum<Integer>{
	WAIT(0,"等待中"),
	RUN(1,"运行中"),
	CLOSE(-1,"结束"),
	END(-2,"处理完成"),
	;

	private Integer code;
	private String message;

	SendTaskStatusEnum(Integer code, String message) {
		this.code = code;
		this.message = message;
	}
}
