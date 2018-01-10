package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2018/1/10 0010 09:24
 * 助通 异常码
 */
@Getter
@AllArgsConstructor
public enum ZhuTongErrorEnum implements CodeEnum<String> {
	SUCCESS("1", "xxxxxxxx	1代表发送短信成功,xxxxxxxx代表消息编号（消息ID,在匹配状态报告时会用到）"),
	FAILED("-1", "xxxxxxxx	发送失败，xxxxxxx代表消息编号"),
	A("2", "username is null	用户名为空"),
	B("3", "username wrong	用户名错误"),
	C("4", "password is null	密码为空"),
	D("5", "password wrong	密码错误"),
	E("6", "tkey is null	当前时间tkey为空"),
	F("7", "tkey error	tkey 当前时间错误"),
	G("8", "username type error	用户类型错误"),
	H("9", "ip error	鉴权错误"),
	I("10", "ip black	IP黑名单"),
	J("11", "product wrong	产品错误（联系客服）"),
	K("12", "product disable	产品禁用（联系客服）"),
	L("13", "mobile wrong	手机号码错误，支持号段参照号段支持"),
	M("15", "Signature format error	签名不合规"),
	N("16", "Signature disable	签名屏蔽"),
	O("17", "xxxxxxxx	代表签名分配扩展失败,20160xxxxx代表消息编号（21位消息ID）。"),
	P("18", "content not null	短信内容不能为空"),
	Q("19", "content max 1000	短信内容最大1000个字"),
	R("20", "NO sum	预付费用户条数不足"),
	S("21", "Have black word:xxx	发送内容存在黑词"),
	T("22", "channel error	通道错误（联系客服）"),
	W("28", "Signature max 15	签名最长15个字"),
	U("29", "xh wrong	小号错误"),
	V("98", "xxxxxxxx	异常（联系客服）"),
	X("99", "DES exception	DES解密Exception"),

	;
	private String code;
	private String message;

}
