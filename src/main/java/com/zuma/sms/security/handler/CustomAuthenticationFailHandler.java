package com.zuma.sms.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zuma.sms.dto.ResultDTO;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.CustomSecurityException;
import com.zuma.sms.util.CodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * author:ZhengXing
 * datetime:2017-11-24 21:03
 * 自定义身份验证失败处理器
 */
@Component("customAuthenticationFailHandler")
@Slf4j
public class CustomAuthenticationFailHandler extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private ObjectMapper objectMapper;


	/**
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {

		CustomSecurityException e1;
		if (e instanceof CustomSecurityException) {
			e1 = (CustomSecurityException) e;
		}else{
			e1 = new CustomSecurityException(ErrorEnum.LOGIN_ERROR.getCode(),e.getMessage());
		}
		log.debug("登录失败.e:{}",e1.getMessage());

		//状态码500会导致ajax无法执行回调
//		response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
		response.getWriter().write(objectMapper.writeValueAsString(ResultDTO.error(e1.getCode(),e1.getMessage())));



	}
}
