package com.springboot.backend.nacho.userapp.users_backend.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringSecurityConfig {

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
		).csrf(config -> config.disable())
				.sessionManagement(mgmt -> mgmt.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.build();
		
	}
	
}
