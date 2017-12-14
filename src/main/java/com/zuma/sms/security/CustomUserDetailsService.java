package com.zuma.sms.security;

import com.zuma.sms.entity.User;
import com.zuma.sms.enums.system.ErrorEnum;
import com.zuma.sms.exception.CustomSecurityException;
import com.zuma.sms.exception.SmsSenderException;
import com.zuma.sms.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 17:08
 * security框架. 从数据库中加载用户
 */
@Component
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsernameEquals(username);
		if(user == null)
			throw new CustomSecurityException(ErrorEnum.USER_NOT_EXIST);


		return new CustomUser(user.getId(),
				username,user.getPassword(),true,
				AuthorityUtils.commaSeparatedStringToAuthorityList("admin")
		);
	}
}
