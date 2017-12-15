package com.zuma.sms.util;


import com.zuma.sms.enums.system.CodeEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import lombok.extern.slf4j.Slf4j;

/**
 * author:ZhengXing
 * datetime:2017/10/18 0018 10:02
 * 枚举工具类
 */
@Slf4j
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
            if (each.getCode().equals(code)) {
                return each;
            }
        }
        return null;
    }


    /**
     * 根据code返回枚举,为空时抛出异常
     * @param code
     * @param enumClass
     * @param logInfo
     * @param logObj
     * @param <T>
     * @param <X>
     * @return
     */
    public static <T extends CodeEnum<X>, X> T getByCode(X code, Class<T> enumClass, String logInfo, Object... logObj) {
        T result;
        if ((result = getByCode(code, enumClass)) == null) {
            log.error(logInfo,logObj);
            throw new SmsSenderException(ErrorEnum.GET_ENUM_ERROR);
        }
        return result;
    }

    /**
     * 某个 code 和某个枚举的的code是否一致
     * 注意,一定要前者equals后者.否则容易空指针
     */
    public static <T extends CodeEnum<X>,X> boolean equals(X code,T enumObj) {
        return enumObj.getCode().equals(code);
    }


}
