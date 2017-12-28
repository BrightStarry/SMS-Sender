package com.zuma.sms.controller.base;

import com.zuma.sms.factory.PageRequestFactory;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.SmsSenderException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * author:ZhengXing
 * datetime:2017/10/18 0018 09:39
 * 基础Controller，复用一些方法
 */
@Controller
@Slf4j
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
	 * 表单验证
	 */
	protected  void isValid(BindingResult bindingResult) {
		isValid(bindingResult,null,null);
	}




	/**
     * String 非空验证
     */
    protected void notEmptyOfString(String... param) {
        for (String temp : param) {
            if (StringUtils.isEmpty(temp)) {
                throw new SmsSenderException(ErrorEnum.FORM_ERROR.getCode(),"参数为空");
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

	protected void commonDownload(@PathVariable Long id, HttpServletResponse response,InputStream inputStream) {
		try (InputStream inputStream1 = inputStream;
			 OutputStream outputStream = response.getOutputStream()) {
			//定义类型和下载过去的文件名
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename=" + id + ".txt");
			//将输入流输出到输出流
			IOUtils.copy(inputStream, outputStream);
			outputStream.flush();
		} catch (Exception e) {
			log.error("下载文件异常.e:{}",e.getMessage(),e);
			throw new SmsSenderException(ErrorEnum.IO_ERROR);
		}
	}


}
