package com.zuma.sms.controller.base;

import com.zuma.sms.config.store.ConfigStore;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.enums.system.ErrorEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * author:ZhengXing
 * datetime:2017/10/17 0017 13:09
 * 异常返回控制类
 */
@Controller
@RequestMapping("/error")
public class ErrorController {

    @Autowired
    private ConfigStore configStore;

    /**
     * ajax请求异常返回
     */
    @ResponseBody
    @RequestMapping(value = "/")
    public ResultDTO<?> commonJson(HttpServletRequest request) {
        return ResultDTO.error((String) request.getAttribute("code"), (String) request.getAttribute("message"));
    }

    /**
     * 普通请求异常返回
     */
    @RequestMapping(value = "/", produces = "text/html")
    public String commonHtml(HttpServletRequest request) {
        return configStore.errorUrl;
    }


    /**
     * 404-页面
     */
    @RequestMapping(value = "/404", produces = "text/html")
    public String error404Html(Model model) {
        model.addAttribute("code", ErrorEnum.NOT_FOUND.getCode());
        model.addAttribute("message", ErrorEnum.NOT_FOUND.getMessage());
        return configStore.errorUrl;
    }

    /**
     * 404-json
     */
    @ResponseBody
    @RequestMapping(value = "/404")
    public ResultDTO<?> error404() {
        return ResultDTO.error(ErrorEnum.NOT_FOUND.getCode(), ErrorEnum.NOT_FOUND.getMessage());
    }
}
