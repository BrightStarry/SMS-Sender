package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 17:36
 * 铭锋异常枚举
 */
@Getter
public enum MingFengErrorEnum implements CodeEnum<String>{
	SUCCESS1("Success","成功"),
	SUCCESS2("1","操作成功"),
	SUCCESS3("10", "成功"),
	FAILED("20", "失败"),
	PARAMERROR("1001","参数错误"),
	USERNAMEEMPTY("1002","用户名为空"),
	PASSWORDEMPTY("1003","密码为空"),
	USERNAMEERROR("1004","用户名错误"),
	PASSWORDERROR("1005","密码错误"),
	BINDIPERROR("1006","IP绑定错误"),
	USERSTOP("1007","帐户已停用"),
	USERIDERROR("1008","UserId参数错误，该值必需要是数字，由供应商提供。"),
	TEXT64ERROR("1009","Text64参数错误，错误的可能有：不是有效的base64编码，Des解密失败，解析json时出错。"),
	STAMPERROR("1010","时间戳错误，可能是格式不对，或是时间偏差太大（应该在5分钟以内）。"),
	CONTENTEMPTY("2001","内容为空"),
	MSISDNEMPTY("1103","手机号码为空"),
	EXTNUMBERERROR("1104","扩展错误"),
	CONTENTLONG("2105","内容太长"),
	NOCHANNEL("1106","没有发送通道"),
	SENSITIVEWORDS("2107","敏感词汇"),
	MSISDNERROR("1108","错误的手机号码"),
	MSISDNBLACK("1109","黑名单的手机号码"),
	MSISDNNOCHANNEL("1110","没有通道的手机号码"),
	AMOUNTNOTENOUGH("1111","额度不足"),
	NOPRODUCT("1112","没有配置产品"),
	REQUIREDSUFFIX("2113","需要签名"),
	SUFFIXERROR("2114","签名错误"),
	SUBJECTEMPTY("3001","主题为空"),
	INTERNALERROR("9999","系统内部错误"),


	;
	private String code;
	private String message;

	MingFengErrorEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
