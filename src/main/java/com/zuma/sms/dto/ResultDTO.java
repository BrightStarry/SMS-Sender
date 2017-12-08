package com.zuma.sms.dto;

import com.zuma.sms.enums.ResultDTOTypeEnum;
import com.zuma.sms.enums.system.CodeEnum;
import com.zuma.sms.enums.system.ErrorEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * author:ZhengXing
 * datetime:2017/10/16 0016 17:02
 * 返回对象
 */


@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ResultDTO<T> {
    /**状态码*/
    private String code;
    /**消息*/
    private String message;
    /**类型*/
    private String type = ResultDTOTypeEnum.COMMON.getCode();
    /**数据*/
    private T data;

    public ResultDTO(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultDTO(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 判断某个对象是否成功
     * @param resultDTO
     * @return
     */
    public static boolean isSuccess(ResultDTO<?> resultDTO){
        return resultDTO.getCode().equals(ErrorEnum.SUCCESS.getCode());
    }

    /**
     *  返回成功状态，以及数据
     */
    public static <T> ResultDTO<T> success(T data) {
        return new ResultDTO<>(ErrorEnum.SUCCESS.getCode(),
                ErrorEnum.SUCCESS.getMessage(),
                data);
    }

    /**
     *  返回成功状态，以及数据
     */
    public static <T> ResultDTO<T> success() {
        return success(null);
    }

    /**
     * 返回成功状态，数据为空
     */
//    public static <T> ResultDTO<T> success(Class<T> tClass){
//        try {
//            return success(tClass.newInstance());
//        } catch (InstantiationException | IllegalAccessException e) {
//            //...
//        }
//        throw new SmsSenderException(ErrorEnum.UNKNOWN_ERROR);
//    }

    /**
     * 返回错误状态， 包含错误状态码和错误消息
     */
    public static ResultDTO<?> error(String code, String msg) {
        return new ResultDTO(code,msg);
    }

    /**
     * 返回错误状态， 包含错误状态码和错误消息
     */
    public static <T> ResultDTO<T> error(String code, String msg,T obj) {
        return new ResultDTO<T>(code,msg,obj);
    }

    /**
     * 返回错误状态,
     */
    public static <T> ResultDTO<T> error(String code, String msg,Class<T> tClass) {
        return new ResultDTO<T>(code,msg);
    }

    /**
     * 返回错误状态
     */
    public static <T> ResultDTO<T> error(CodeEnum<String> errorEnum, T obj) {
        return new ResultDTO<T>(errorEnum.getCode(),errorEnum.getMessage(),obj);
    }

    /**
     * 返回错误状态,以及返回类型
     */
    public static <T> ResultDTO<T> error(CodeEnum<String> errorEnum,T obj,ResultDTOTypeEnum typeEnum) {
        return new ResultDTO<T>(errorEnum.getCode(),errorEnum.getMessage(),typeEnum.getCode(),obj);
    }

    /**
     * 返回错误状态
     */
    public static <T> ResultDTO<T> errorOfInteger(CodeEnum<Integer> errorEnum,T obj) {
        return new ResultDTO<T>(String.valueOf(errorEnum.getCode()),errorEnum.getMessage(),obj);
    }


}
