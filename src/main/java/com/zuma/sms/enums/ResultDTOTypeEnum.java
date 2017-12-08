package com.zuma.sms.enums;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:Administrator
 * datetime:2017/11/10 0010 10:14
 * 返回对象类型枚举
 *
 * 该类是为了表示返回对象所属的类型.
 * 需要注意的是,如果调用发送短信接口,同步收到的不是SEND_SMS_CALLBACK_SYNC,
 * 表明该次调用全部失败.
 * 如果是SEND_SMS_CALLBACK_SYNC,则会携带失败数据
 */
@Getter
public enum  ResultDTOTypeEnum implements CodeEnum<String> {
    COMMON("1","通用（无意义）"),
    SEND_SMS_CALLBACK_ASYNC("2","发送短信异步回调"),
    SMS_UP("3","短信上行推送"),
    SEND_SMS_CALLBACK_SYNC("3","发送短信同步回调"),
    ;
    private String code;
    private String message;

    ResultDTOTypeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
