package com.zuma.sms.enums.system;

import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/12/19 0019 15:35
 * 通道枚举 
 */
@Getter
public enum ChannelEnum implements CodeEnum<String> {

	ZHANGYOU_YD("zhangYouYD", "掌游_移动"),
	KUANXIN_YD("kuanXinYD", "宽信_移动"),
	KUANXIN_DX("kuanXinDX", "宽信_电信"),
	KUANXIN_CMPP("kuanXinCMPP", "宽信_CMPP"),
	QUNZHENG_YD("qunZhengYD", "群正_移动"),
	ZHUWANG_CMPP("zhuWangCMPP", "筑望_CMPP"),
	CHANGXIANG_YD("changXiangYD", "畅想_移动"),
	CHUANGLAN_YD("chuangLanYD", "创蓝_移动"),
	MINGFENG_YD("mingFengYD", "铭锋_移动"),

	;

	private String code;
	private String message;

	ChannelEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
}
