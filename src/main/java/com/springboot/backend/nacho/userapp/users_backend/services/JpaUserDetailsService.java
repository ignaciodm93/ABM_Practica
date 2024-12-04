package com.springboot.backend.nacho.userapp.users_backend.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springboot.backend.nacho.userapp.users_backend.repositories.UserRepository;

@Service
public class JpaUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Transactional(readOnly = true)
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<com.springboot.backend.nacho.userapp.users_backend.entities.User> optionalUser = userRepository.findByUsername(username);
		if (optionalUser.isEmpty()) {
			throw new UsernameNotFoundException(String.format("Username %s no existe en el sistema", username));
		}
		
		com.springboot.backend.nacho.userapp.users_backend.entities.User user = optionalUser.orElseThrow();
		//Se castean los roles del user de la bd a GrantedAuthority
		List<GrantedAuthority> auth = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
		
		//Se retorna el objeto UserDetails, siendo el usuario de bd con los roles casteados a roles de spring.
		return new org.springframework.security.core.userdetails.User(username, user.getPassword(), true, true, true, true, auth);
	}

	
	
}
