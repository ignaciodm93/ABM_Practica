package com.springboot.backend.nacho.userapp.users_backend.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.springboot.backend.nacho.userapp.users_backend.auth.filter.JwtAuthenticationFilter;

@Configuration
public class SpringSecurityConfig {

	@Autowired
	private AuthenticationConfiguration authenticationConfiguration;
	
	@Bean
	AuthenticationManager authenticationManager() throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		//Configuración de las rutas permitidas por rol
		return http.authorizeHttpRequests(authz -> 
		authz
		.requestMatchers(HttpMethod.GET, "/api/users", "/api/users/page/{page}").permitAll()
		.requestMatchers(HttpMethod.GET, "/api/users{id}").hasAnyRole("ADMIN", "USER")
		.requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN")
		.requestMatchers(HttpMethod.PUT, "/api/users/{id}").hasRole("ADMIN")
		.requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasRole("ADMIN")
		.anyRequest().authenticated()
		).addFilter(new JwtAuthenticationFilter(authenticationManager())).csrf(config -> config.disable())
				.sessionManagement(mgmt -> mgmt.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
		
	}
	
}
