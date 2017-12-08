package com.zuma.sms.exception;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/11/7 0007 16:40
 */
@Getter
public class SmsSenderException extends RuntimeException {
    private String code;

    /**
     * 根据异常枚举构造自定义异常
     * @param codeEnum
     */
    public SmsSenderException(CodeEnum<String> codeEnum){
        super(codeEnum.getMessage());
        this.code = codeEnum.getCode();
    }

    /**
     * 根据异常码和消息构造自定义异常
     * @param code
     * @param message
     */
    public SmsSenderException(String code, String message) {
        super(message);
        this.code = code;
    }
}
