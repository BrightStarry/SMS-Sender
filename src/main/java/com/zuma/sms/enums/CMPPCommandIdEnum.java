package com.zuma.sms.enums;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/11/23 0023 15:23
 * cmpp commandId 对应枚举
 */
@Getter
public enum CMPPCommandIdEnum implements CodeEnum<Integer> {
    CMPP_CONNECT(0x00000001, "请求连接"),
    CMPP_CONNECT_RESP(0x80000001, "请求连接应答"),
    CMPP_SUBMIT(0x00000004, "提交短信"),
    CMPP_SUBMIT_RESP(0x80000004, "提交短信应答"),
    CMPP_DELIVER(0x00000005, "短信下发"),
    CMPP_DELIVER_RESP(0x80000005, "短信下发应答"),
    CMPP_ACTIVE_TEST(0x00000008, "激活测试"),
    CMPP_ACTIVE_TEST_RESP(0x80000008, "激活测试应答"),

    UNKNOWN(0X00000000, "未知"),



    ;

    private Integer code;
    private String message;

    CMPPCommandIdEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
