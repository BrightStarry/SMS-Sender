package com.zuma.sms.enums.system;

import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 10:48
 * 配置模块枚举, 和字典表中的module字段对应
 */
@Getter
public enum ConfigModuleEnum implements CodeEnum<String>{
	COMMON("common","通用模块"),
	RUN("run", "运行模块"),


	;
	private String code;
	private String message;

	ConfigModuleEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
