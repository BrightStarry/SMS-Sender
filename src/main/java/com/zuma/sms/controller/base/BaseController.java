package com.zuma.sms.controller.base;

import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

/**
 * author:ZhengXing
 * datetime:2017/10/18 0018 09:39
 * 基础Controller，复用一些方法
 */
@Controller
public class BaseController {
    protected static final String navTop1 =  "navTop1";
    protected static final String navTop2 =  "navTop2";

    @Autowired
    private ConfigStore configStore;

    @Autowired
    private PageRequestFactory pageRequestFactory;


    /**
     * 表单验证
     * @param bindingResult 检验返回对象
     * @param log slf4j日志对象
     * @param logInfo 日志内容
     * @param logObject 日志输出对象
     */
    protected void isValid(BindingResult bindingResult, Logger log, String logInfo, Object... logObject) {
        //如果校验不通过,记录日志，并抛出异常
        if (bindingResult.hasErrors()) {
            if(StringUtils.isNotBlank(logInfo))
                log.error(logInfo, logObject);
            throw new SmsSenderException(ErrorEnum.FORM_ERROR.getCode(), bindingResult.getFieldError().getDefaultMessage());
        }
    }


    /**
     * String 非空验证
     */
    protected void notEmptyOfString(String... param) {
        for (String temp : param) {
            if (StringUtils.isEmpty(temp)) {
                throw new SmsSenderException(ErrorEnum.FORM_ERROR);
            }
        }
    }

    /**
     * long 非0校验
     */
    protected void notZeroOfLong(long... param) {
        for (long temp : param) {
            if (temp == 0) {
                throw new SmsSenderException(ErrorEnum.FORM_ERROR);
            }
        }
    }

    /**
     * 分页请求对象拼接
     */
    protected Pageable getPageRequest(int pageNo, Integer pageSize) {
        return pageRequestFactory.buildForCommon(pageNo, pageSize);
    }



}
