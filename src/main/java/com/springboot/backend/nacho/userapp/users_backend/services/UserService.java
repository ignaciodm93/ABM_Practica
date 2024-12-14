package com.springboot.backend.nacho.userapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.springboot.backend.nacho.userapp.users_backend.entities.User;
import com.springboot.backend.nacho.userapp.users_backend.entities.UserRequest;

public interface UserService {
	
	List<User> findAll();
	
	Optional<User> findById(@NonNull Long id);
	
	User save(User user);
	
	void deleteById(Long id);
	
	Page<User> findAll(Pageable pageable);
	
	Optional<User> update(UserRequest user, Long id);
}
