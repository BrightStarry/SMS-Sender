package com.zuma.sms.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 16:51
 * springSecurity配置
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	//验证成功处理器
	@Autowired
	private AuthenticationSuccessHandler customAuthenticationSuccessHandler;
	//验证失败处理器
	@Autowired
	private AuthenticationFailureHandler customAuthenticationFailHandler;

	//数据源
	@Autowired
	private DataSource dataSource;

	//记住我功能的配置,需要注入
	@Autowired
	private UserDetailsService customUserDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	/**
	 * 记住我功能
	 * 生成用来将token写入数据库的PersistentTokenRepository类
	 */
	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		//设置在启动时,创建对应的数据库中存储token的表,只需要第一次启动时使用,或者是进去复制创表语句
//        tokenRepository.setCreateTableOnStartup(true);
		return tokenRepository;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.formLogin()
					.loginPage("/user/view/login")
					.loginProcessingUrl("/user/login")
					.successHandler(customAuthenticationSuccessHandler)//配置验证成功处理器
					.failureHandler(customAuthenticationFailHandler)//配置验证失败处理器
				.and()
				.logout()
					.logoutUrl("/user/exit")
					.logoutSuccessUrl("/user/view/login")
				.and()
				.rememberMe()
					//token仓库配置,用来将token存入数据库
					.tokenRepository(persistentTokenRepository())
					//token过期秒数配置
					.tokenValiditySeconds(3600)
					//查询用户信息的service
					.userDetailsService(customUserDetailsService)
				.and()
				.authorizeRequests()//进行验证配置
				.antMatchers("/user/view/login",
						"/user/login")//匹配这些路径
				.permitAll()//全部允许
				.anyRequest()//任何请求
				.authenticated();//都需验证

		http.csrf().disable();//暂时关闭csrf,防止跨域请求的防护关闭
	}
}
