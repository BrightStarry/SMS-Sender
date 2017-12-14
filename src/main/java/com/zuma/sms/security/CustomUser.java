package com.zuma.sms.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * author:ZhengXing
 * datetime:2017/12/12 0012 12:08
 * 自定义用户类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomUser implements UserDetails {

	private Long id;

	private String username;

	private String password;

	private Boolean enabled;

	private Collection<? extends GrantedAuthority> authorities;

	public CustomUser(Long id, String username, String password, Boolean enabled) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.enabled = enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}
}
