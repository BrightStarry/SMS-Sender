package com.zuma.sms.enums;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:Administrator
 * datetime:2017/11/10 0010 09:48
 */
@Getter
public enum ZhangYouCallbackMsgTypeEnum implements CodeEnum<String> {

    MO("1","mo"),
    REPORT("2","report")
    ;
    private String code;
    private String message;

    ZhangYouCallbackMsgTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
