package com.zuma.sms.controller.base;

import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * author:ZhengXing
 * datetime:2017/10/17 0017 10:28
 * 通用异常处理类
 */
@ControllerAdvice
@Slf4j
public class CommonExceptionHandler {
    /**
     * 自定义异常处理
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(SmsSenderException.class)
    public String customExceptionHandle(SmsSenderException e, HttpServletRequest request) {
        setCodeAndMessage(request,e.getCode(),e.getMessage());
        return "forward:/error/";
    }

    /**
     * 未受检异常处理
     * @param e
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public String exceptionHandler(Exception e, HttpServletRequest request) {
        log.error("【异常处理】未知异常,error={}",e.getMessage(),e);
        setCodeAndMessage(request, ErrorEnum.UNKNOWN_ERROR.getCode(),ErrorEnum.UNKNOWN_ERROR.getMessage());
        return "forward:/error/";
    }

    /**
     * 给request设置code和message属性
     * @param request
     * @param code
     * @param message
     */
    private void setCodeAndMessage(HttpServletRequest request, String code, String message) {
        request.setAttribute("code",code);
        request.setAttribute("message",message);
    }
}
