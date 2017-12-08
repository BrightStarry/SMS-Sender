package com.zuma.sms.util;


import com.zuma.sms.enums.system.CodeEnum;

/**
 * author:ZhengXing
 * datetime:2017/10/18 0018 10:02
 * 枚举工具类
 */
public class EnumUtil {

    /**
     * 根据Code返回枚举
     * @param code
     * @param enumClass
     * @param <T>
     * @return
     */
    public static <T extends CodeEnum<X>,X> T getByCode(X code, Class<T> enumClass) {
        for (T each : enumClass.getEnumConstants()) {
            if (code.equals(each.getCode())) {
                return each;
            }
        }
        return null;
    }

    /**
     * 某个 code 和某个枚举的的code是否一致
     */
    public static <T extends CodeEnum<X>,X> boolean equals(Integer code,T enumObj) {
        return code.equals(enumObj.getCode());
    }


}
