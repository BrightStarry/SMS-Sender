package com.zuma.sms.enums.system;

import lombok.Getter;

/**
 * author:ZhengXing
 * datetime:2017/11/7 0007 16:42
 * 异常状态枚举
 */
@Getter
public enum ErrorEnum implements CodeEnum<String> {
    SUCCESS("0000","成功"),
    UNKNOWN_ERROR("0001","发生未知异常"),
    HTTP_ERROR("0002","http请求失败"),

    PLATFORM_EMPTY("0004","平台不存在"),
    CHANNEL_EMPTY("0005","通道不存在"),
    PHONE_UNKNOWN("0006","存在无法识别手机号"),
    PHONE_NUMBER_OVER("0007","手机号数目超限"),
    SMS_LEN_AND_PHONE_LEN_MISMATCH("0008","短信数和手机号数不匹配，必须为一对一或一对多或多对多且数目相同"),
    UNSUPPORTED_OPERATOR("0009","包含指定通道不支持的运营商"),
    HTTP_RESPONSE_IO_ERROR("0010","http请求response对象，转换时io异常"),
    HTTP_STATUS_CODE_ERROR("0011","http请求状态码非200"),
    STRING_TO_RESPONSE_ERROR("0012","短信API返回参数有误"),
    SEND_SMS_ERROR("0013","短信发送异常"),
    TRANSCODE_ERROR("0014","转码失败"),
    IP_UNALLOW("0015","IP非法"),
    CACHE_EXPIRE("0016","缓存过期,无法解析发送短信接口异步回调信息"),
    SEND_CALLBACK_TO_PLATFORM_ERROR("0017", "发送回调请求到平台失败"),
    SIGN_NOT_MATCH_ERROR("0018","签名不匹配"),
    POOL_ERROR("0019","对象池异常"),
    ZHUWANG_CONNECT_ERROR("0020", "连接筑望服务器失败"),
    MD5_SIGN_ERROR("0021", "MD5签名异常"),
    ZHUWANG_RESPONSE_MESSAGE_ERROR("0022","筑望服务器响应异常"),
    SOCKET_REQUEST_ERROR("0023","socket请求失败"),
    ENCODE_ERROR("0024","消息对象编码异常"),
    NON_AVAILABLE_SOCKET("0025","该平台没有可用socket连接"),
    NOT_FOUND("0026","没有该路径"),


    //登录
    LOGIN_ERROR("1000","登陆异常"),
    USER_NOT_EXIST("1001","用户不存在"),

    //任务线程
    TASK_START_ERROR("2001","任务启动失败"),
    TASK_DELAY_TAKE_ERROR("2002", "取出延时任务失败"),
    TASK_EMPTY_IN_WAIT_QUEUE_ERROR("2003","任务在等待队列不存在"),

    //controller
    FORM_ERROR("3000","参数校验失败"),
    UPLOAD_FILE_EMPTY("3001","上传文件为空"),
    UPLOAD_FILE_TOO_MANY("3002","上传文件过多"),
    ARRAY_EMPTY("3003","数组为空"),



    //service
    IO_ERROR("4001","文件读取异常"),
    OBJECT_EMPTY("4002", "对象不存在"),


    //业务
    UPLOAD_MULTI_FORMAT_ERROR("4001","多文件上传,其他参数格式不匹配"),
    NUMBER_SOURCE_PHONE_EMPTY("4002", "号码源数量为空"),
    NUMBER_SOURCE_PHONE_FORMAT_ERROR("4003", "号码格式不正确"),
    RELEVANCE_PARAM_MISMATCHING("4004", "关联参数不匹配"),







    ;
    private String code;
    private String message;

    ErrorEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
