package com.springboot.backend.nacho.userapp.users_backend.auth.filter;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.nacho.userapp.users_backend.auth.SimpleGrantedAuthorityJsonCreator;
import com.springboot.backend.nacho.userapp.users_backend.auth.TokenJwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.lang.Arrays;

public class JwtValidationFilter extends BasicAuthenticationFilter {

	public JwtValidationFilter(AuthenticationManager authenticationManager) {
		super(authenticationManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
		String header = request.getHeader(TokenJwtConfig.HEADER_AUTHORIZATION);
		
		if(header == null || !header.startsWith(TokenJwtConfig.PREFIX_TOKEN)) {
			chain.doFilter(request,  response);
			return;
		}
		
		String token = header.replace(TokenJwtConfig.PREFIX_TOKEN, "");
		try {
			Claims claims = Jwts.parser().verifyWith(TokenJwtConfig.SECRET_KEY).build().parseSignedClaims(token).getPayload();			
			String username = claims.getSubject();
			Object authoritiesClaims = claims.get("authorities");
			
			Collection<? extends GrantedAuthority> roles = Arrays.asList(new ObjectMapper().addMixIn(SimpleGrantedAuthority.class, SimpleGrantedAuthorityJsonCreator.class) .readValue(authoritiesClaims.toString().getBytes(), SimpleGrantedAuthority[].class));
			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, roles);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			chain.doFilter(request, response);
		} catch(JwtException e) {
			Map<String, String> body = new HashMap<>();
			body.put("error", e.getMessage());
			body.put("message", "El token es invalido");
			
			response.getWriter().write(new ObjectMapper().writeValueAsString(body));
			response.setStatus(401);
			response.setContentType(TokenJwtConfig.CONTENT_TYPE);
		}
		
	}
	
}
