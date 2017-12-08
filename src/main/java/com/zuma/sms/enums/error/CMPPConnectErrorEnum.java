package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/11/23 0023 13:28
 * cmpp 连接接口 异常码
 */
@Getter
public enum CMPPConnectErrorEnum implements CodeEnum<Integer>{
    SUCCESS(0, "正确"),
    A(1, "消息结构错误"),
    B(2, "非法源地址"),
    C(3, "认证错误"),
    D(4, "版本太高"),
    OTHER_ERROR(5, "其他错误"),
    ;
    private Integer code;
    private String message;

    CMPPConnectErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
