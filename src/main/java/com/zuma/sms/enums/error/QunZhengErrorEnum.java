package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017-11-09 19:21
 */
@Getter
public enum QunZhengErrorEnum implements CodeEnum<String>{

    SUCCESS("0", "成功"),
    A("-1", "帐号密码错误"),
    B("-2", "账号被停用"),
    C("-3", "账号类型不允许使用"),
    D("-4", "手机号码为空"),
    E("-5", "内容为空"),
    F("-6", "内容包含黑字典字样"),
    G("-7", "通道分配错误"),
    H("-8", "余额不足"),
    I("-9", "定时时间格式错误 2013-03-25 08:30:00"),
    J("-10", "批次号码为空"),
    K("-11", "当前批次尚未生成发送明细表"),
    L("-12", "新密码不能为空"),
    M("-13", "当前密码不符合系统规范"),
    N("-20", "签名不存在或者不符合要求（签名前两个字必须为汉字）"),
    O("-100", "未知错误"),

    CALLBACK_SUCCESS("10","成功"),
    Q("11","失败")
    ;
    private String code;
    private String message;

    QunZhengErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
