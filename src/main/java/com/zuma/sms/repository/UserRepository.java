package com.zuma.sms.repository;

import com.zuma.sms.entity.Dict;
import com.zuma.sms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * author:ZhengXing
 * datetime:2017/12/5 0005 12:41
 * 用户
 */
public interface UserRepository extends JpaRepository<User,Long> {
	/**
	 * 根据用户名查询用户
	 */
	User findByUsernameEquals(String username);
}
