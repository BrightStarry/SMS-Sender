package com.zuma.sms.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuma.sms.config.ConfigStore;
import com.zuma.sms.dto.ResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017-11-24 20:40
 * 自定义身份验证成功处理器
 * security默认在验证成功后跳转到此前访问的页面,但是如果前端的登录是
 * ajax方式的,不适合跳转页面,所以需要更改成功后的处理
 */
@Component("customAuthenticationSuccessHandler")
@Slf4j
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
	/**
	 * springMVC在启动时自动注册的bean,用于将对象转为json
	 */
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ConfigStore configStore;

	/**
	 * 当登陆成功时
	 *
	 * @param request
	 * @param response
	 * @param authentication 封装了认证信息
	 */
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		log.info("登录成功");
//		//登录成功后取出用户信息
//		CustomUser user = (CustomUser) authentication.getPrincipal();
//		//自己换个key存入session中
//		request.getSession().setAttribute(configStore.sessionUserKey,user);

		//如果配置的的登录方式是json,使用自定义处理器
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		//返回
		response.getWriter().write(objectMapper.writeValueAsString(ResultDTO.success()));
	}
}
