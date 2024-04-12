package com.smartContactManager.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smartContactManager.entities.User;
import com.smartContactManager.repositories.UserRepository;

public class UserDetailsServiceImpl implements UserDetailsService{
	@Autowired
	private UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			User userByUserName = userRepository.getUserByUserName(username);

			if(userByUserName == null)
			{
				throw new UsernameNotFoundException("username not found");
			}
			CustomUserDetail customUserDetail = new CustomUserDetail(userByUserName);
		return customUserDetail;
	}

}
