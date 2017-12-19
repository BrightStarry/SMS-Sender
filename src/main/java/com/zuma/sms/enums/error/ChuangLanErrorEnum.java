package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 13:16
 */
@Getter
public enum ChuangLanErrorEnum implements CodeEnum<String>{


  	SUCCESS("0","提交成功"),
	A("101","无此用户"),
	B("102","密码错"),
	C("107","包含错误的手机号码"),
	D("110","不在发送时间内"),
	E("119","用户已过期"),
	F("123","发送类型错误"),
	G("124","白模板匹配错误"),
	H("127","定时发送时间格式错误"),
	I("128","内容编码失败"),
	J("129","JSON格式错误"),
	K("125","匹配驳回模板,提交失败"),
	L("117","IP地址认证错,请求调用的IP地址不是系统登记的IP地址"),
	M("103","提交过快(提交速度超过流速限制)"),
	N("104","系统忙（因平台侧原因，暂时无法处理提交的短信）"),
	O("106","消息长度错（>536或<=0）"),
	P("109","无发送额度（该用户可用短信数已使用完）"),
	Q("105","敏感短信（短信内容包含敏感词）"),
	R("108","手机号码个数错（群发>50000或<=0）"),
	S("114","可用参数组个数错误（小于最小设定值或者大于1000）;变量参数组大于20个"),
	T("113","扩展码格式错（非数字或者长度不对）"),
	U("116","签名不合法或未带签名（用户必须带签名的前提下）"),
	V("120","违反防盗用策略(日发送限制)"),
	W("130","请求参数错误（缺少必填参数）"),
	X("118","用户没有相应的发送权限（账号被禁止发送）"),
	Y("DELIVRD","短信发送成功"),
	Z("UNKNOWN","未知短信状态"),
	A1("REJECTD","短信被短信中心拒绝"),
	A2("MBBLACK","目的号码是黑名单号码"),
	A3("REJECT"	,"审核驳回"),



	;

	private String code;
	private String message;

	ChuangLanErrorEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
