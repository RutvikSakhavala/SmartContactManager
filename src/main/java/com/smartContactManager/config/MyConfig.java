package com.smartContactManager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class MyConfig{

	@Bean
	UserDetailsService getUserDetailsService()
	{
		return new UserDetailsServiceImpl();
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
//
	  @Bean
	  DaoAuthenticationProvider authenticationProvider() {

		  DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
	  daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());
	  daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

	  return daoAuthenticationProvider;

	  }
//
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

		http
		     .csrf(csrf -> csrf.disable())
		     .authorizeHttpRequests(authorize -> authorize
		    		 .requestMatchers("/user/**").hasRole("USER")
		    		 .requestMatchers("/admin/**").hasRole("ADMIN")
		    		 .requestMatchers("/**").permitAll()
		    		 .anyRequest().authenticated()
		    		 )
		     .formLogin(formLogin -> formLogin.loginPage("/login").defaultSuccessUrl("/user/index"));

		http.authenticationProvider(authenticationProvider());


		return http.build();
	}
//
}

