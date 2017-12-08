package com.zuma.sms.enums;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:Administrator
 * datetime:2017/11/8 0008 15:11
 * 短信消息-手机号对应关系枚举
 */
@Getter
public enum SmsAndPhoneRelationEnum implements CodeEnum<Integer> {

    ONE_ONE(1,"一对一，一个短信对一个手机号"),
    ONE_MULTI(2,"一对多，一个短信对多个手机号"),
    MULTI_MULTI(3,"多对多，多个短信对多个手机号，且一一对应"),
    OTHER(0,"其他，不符合规范的关系")
    ;
    private Integer code;
    private String message;

    SmsAndPhoneRelationEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
