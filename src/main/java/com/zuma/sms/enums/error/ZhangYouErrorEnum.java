package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:Administrator
 * datetime:2017/11/7 0007 16:48
 * 掌游平台异常码
 */
@Getter
public enum ZhangYouErrorEnum implements CodeEnum<String> {

    SUCCESS("1000","请求成功"),
    A("1001","合作商信息不存在或已过期"),
    B("1002","业务代码不存在"),
    C("1003","未授权的IP地址"),
    D("1004","安全签名校验失败"),
    E("1005","超出单次下发条数限制"),
    F("1006","手机号码不正确"),
    G("1007","短信内容有误(超长,或者为空等)"),
    H("1008","短信发送失败"),
    I("1009","加长接入号长度错误"),
    J("1010","参数个数不对(socket接口)"),
    K("1011","包头信息不对(socket接口)"),
    L("1012","包尾信息不对(socket接口)"),
    M("1013","余额不足"),
    N("1014","彩信标题有误(超长,或者为空等)"),
    O("1015","该业务在本时间段限制发送"),
    P("1016","短信内容包含非法关键字"),
    Q("1050","其他错误，请联系我方技术人员"),


    //异步回调状态
    R("5000","信息下发成功"),
    S("5001","信息下发失败"),
    T("6000","普通消息上行"),
    U("9999", "其它错误"),

    //我们回应状态
    RESPONSE_SUCCESS("9000","处理成功"),
    RESPONSE_FAILED("9001","处理失败")
    ;

    private String code;
    private String message;

    ZhangYouErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
