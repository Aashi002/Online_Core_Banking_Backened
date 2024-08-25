package com.src.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class JwtConfig {
	@Autowired
	private JwtFilter filter;

	@Bean
	public UserDetailsService userDetailsService() {
		return new MyUserService();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf().disable()
				.authorizeHttpRequests().requestMatchers("/transaction/**").hasAuthority("ROLE_CUSTOMER")
				.and()
				.authorizeHttpRequests().requestMatchers("/transactionByManager/**").hasAuthority("ROLE_MANAGER")
				.and()
				.authorizeHttpRequests().requestMatchers("/transactionByEmployee/**").hasAuthority("ROLE_EMPLOYEE")
				.and()
				.authorizeHttpRequests().requestMatchers("/giftCard/**").hasAuthority("ROLE_CUSTOMER")
				.and()
				
//				.authorizeHttpRequests().anyRequest().permitAll()
//				.and()
				.exceptionHandling()
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and().authenticationProvider(provider())
				.addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	@Bean
	public AuthenticationProvider provider() {
		DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService());
		provider.setPasswordEncoder(passwordEncoder());
		return provider;
	}
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}
