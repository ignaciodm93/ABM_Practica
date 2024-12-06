package com.springboot.backend.nacho.userapp.users_backend.auth.filter;

import static com.springboot.backend.nacho.userapp.users_backend.auth.TokenJwtConfig.CONTENT_TYPE;
import static com.springboot.backend.nacho.userapp.users_backend.auth.TokenJwtConfig.HEADER_AUTHORIZATION;
import static com.springboot.backend.nacho.userapp.users_backend.auth.TokenJwtConfig.PREFIX_TOKEN;
import static com.springboot.backend.nacho.userapp.users_backend.auth.TokenJwtConfig.SECRET_KEY;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.backend.nacho.userapp.users_backend.entities.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	private AuthenticationManager authenticationManager;
	
	public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String username = null;
		String password = null;
		
		try {
			User user = mapRequestToUser(request);
			username = user.getUsername();
			password = user.getPassword();
		} catch (StreamReadException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DatabindException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		UsernamePasswordAuthenticationToken userAuthToken = new UsernamePasswordAuthenticationToken(username, password);
		
		return this.authenticationManager.authenticate(userAuthToken);
	}

	private User mapRequestToUser(HttpServletRequest request)
			throws IOException, StreamReadException, DatabindException {
		return new ObjectMapper().readValue(request.getInputStream(), User.class);
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User) authResult.getPrincipal();
		String username = user.getUsername();
		Collection<? extends GrantedAuthority> roles = authResult.getAuthorities();
		Claims claims = Jwts.claims().add("authorities", new ObjectMapper().writeValueAsString(roles)).add("username", username).build();
		
		String jwt = getJsonWebToken(username, claims);
		
		buildResponse(response, username, jwt);
	}

	private void buildResponse(HttpServletResponse response, String username, String jwt)
			throws IOException, JsonProcessingException {
		response.addHeader(HEADER_AUTHORIZATION, PREFIX_TOKEN + jwt);
		
		Map<String, String> body = new HashMap<>();
		body.put("token", jwt);
		body.put("username", username);
		body.put("message", "Has iniciado sesión con éxito");
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setContentType(CONTENT_TYPE);
		response.setStatus(200);
	}

	//Configuro y creo el jwt a partir del username, la secret key para firmarlo y las definiciones extras
	private String getJsonWebToken(String username, Claims claims) {
		return Jwts.builder().subject(username).claims(claims).signWith(SECRET_KEY).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + 3600000)).compact();
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		Map<String, String> body = new HashMap<>();
		body.put("message", "Error en la autenticaciòn con username o password incorrecto.");
		body.put("error", failed.getMessage());
		
		response.getWriter().write(new ObjectMapper().writeValueAsString(body));
		response.setContentType(CONTENT_TYPE);
		response.setStatus(401);
	}
	

}
