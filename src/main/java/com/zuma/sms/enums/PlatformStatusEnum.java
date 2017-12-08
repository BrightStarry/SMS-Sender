package com.zuma.sms.enums;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/10/17 0017 09:13
 * 平台状态
 */
@Getter
public enum PlatformStatusEnum implements CodeEnum<Integer> {
    VALID(1,"有效"),
    INVALID(0,"无效");

    private Integer code;
    private String message;

    PlatformStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
