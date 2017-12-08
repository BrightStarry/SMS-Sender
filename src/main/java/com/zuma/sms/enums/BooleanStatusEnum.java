package com.zuma.sms.enums;

import com.zuma.sms.enums.system.CodeEnum;
import lombok.Getter;

/**
 * author:Administrator
 * datetime:2017/11/10 0010 15:30
 */
@Getter
public enum BooleanStatusEnum implements CodeEnum<Integer> {
    TRUE(1,"是",true),
    FALSE(0,"否",false),
    OTHER(2,"待定",false)
    ;

    private Integer code;
    private String message;
    private Boolean isSuccess;

    BooleanStatusEnum(Integer code, String message,Boolean isSuccess) {
        this.code = code;
        this.message = message;
    }
}
