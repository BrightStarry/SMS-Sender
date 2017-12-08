package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:Administrator
 * datetime:2017/11/9 0009 17:08
 * 宽信枚举
 */
@Getter
public enum KuanXinErrorEnum implements CodeEnum<String>{
    CALLBACK_SUCCESS("DELIVRD","成功"),
    SUCCESS("0","成功"),
    A("1","提交失败"),
    B("2","参数校验失败，参数不能为空"),
    C("3","余额不足"),
    D("4","用户不存在"),
    E("5","发送条数不足"),
    F("6","sign校验失败"),
    G("7","IP校验失败"),
    H("8","time时间格式错误"),
    J("9","扩展号格式错误"),
    K("10","发送内容包含敏感词[**]"),
    L("11","签名信息鉴权失败"),
    M("12","短信长度超过限制"),
    N("13","短信内容正在审核中"),
    O("14","ts校验有误，要求5分钟内有效"),
    ;

    private String code;
    private String message;

    KuanXinErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
