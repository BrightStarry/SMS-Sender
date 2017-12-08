package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/4 0004 15:57
 */
@Getter
public enum ChangXiangErrorEnum implements CodeEnum<String> {
	SUCCESS("success","成功"),
	ERROR("error","失败"),

	A("101", "缺少name参数"),
	B("102", "缺少seed参数"),
	C("103", "缺少key参数"),
	D("104", "缺少dest参数"),
	E("105", "缺少content参数"),
	F("106", "seed错误"),
	G("107", "key错误"),
	H("108", "ext错误"),
	I("109", "内容超长"),
	J("110", "模板未备案"),
	K("111", "无签名"),
	L("112", "缺少pk_total参数"),
	M("113", "签名超长"),
	N("114", "定时时间格式错误"),
	O("115", "定时时间范围错误"),
	P("116", "不支持HTTP"),
	Q("117", "不支持CMPP"),
	R("118", "不支持CDN"),
	S("201", "无对应账户"),
	T("202", "账户暂停"),
	U("203", "账户删除"),
	V("204", "账户IP没备案"),
	W("205", "账户无余额"),
	X("206", "密码错误"),
	Y("302", "产品暂停"),
	Z("303", "产品删除"),
	A1("304", "产品不在服务时间"),
	A2("305", "无匹配通道"),
	A3("306", "通道暂停"),
	A4("307", "通道已删除"),
	A5("308", "通道不在服务时间"),
	A6("309", "未提供短信服务"),
	A7("310", "未提供彩信服务"),
	A8("311", "未提供语言外呼服务"),
	A9("301", "无对应产品"),
	A10("401", "屏蔽词"),

	;


	private String code;
	private String message;

	ChangXiangErrorEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
