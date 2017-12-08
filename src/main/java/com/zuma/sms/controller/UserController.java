package com.zuma.sms.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 17:28
 */
@Controller
@RequestMapping("/user")
public class UserController {

	@GetMapping("/view/login")
	public String viewLogin() {
		return "login";
	}
}
