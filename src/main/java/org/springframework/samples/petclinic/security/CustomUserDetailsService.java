package org.springframework.samples.petclinic.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 간단한 사용자 인증을 위해 하드코딩된 사용자 정보를 사용
		// 실제 프로덕션에서는 데이터베이스에서 사용자 정보를 조회해야 합니다
		if ("admin".equals(username)) {
			return new User("admin", passwordEncoder.encode("password"),
					Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		}
		else if ("user".equals(username)) {
			return new User("user", passwordEncoder.encode("password"),
					Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
		}
		else {
			throw new UsernameNotFoundException("User not found: " + username);
		}
	}

}
