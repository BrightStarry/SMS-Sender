package com.zuma.sms.enums.error;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/11/23 0023 17:49
 * CMPP 提交短信异常
 */
@Getter
public enum CMPPSubmitErrorEnum implements CodeEnum<Integer> {
    SUCCESS(0, "正确"),
    A(1, "消息结构错误"),
    B(2, "命令字错误"),
    C(3, "消息序号重复"),
    D(4, "消息长度错误"),
    E(5, "资费代码错误"),
    F(6, "超过最大信息长度"),
    G(7, "业务代码错误"),
    H(8, "流量控制错误"),
    I(9, "本网关不负责此计费号码"),
    J(10, "Src_ID错"),
    K(11, "Msg_src错"),
    L(12, "计费地址错"),
    M(13, "目的地址错"),
    UNCONNECT(51, "尚未建立连接"),
    OTHER_ERROR(9, "其他错误"),
    ;
    private Integer code;
    private String message;

    CMPPSubmitErrorEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
