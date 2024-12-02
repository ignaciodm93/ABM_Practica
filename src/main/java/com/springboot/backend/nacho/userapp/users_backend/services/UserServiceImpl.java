package com.springboot.backend.nacho.userapp.users_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.nacho.userapp.users_backend.entities.User;
import com.springboot.backend.nacho.userapp.users_backend.repositories.UserRepository;

import io.micrometer.common.lang.NonNull;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository repository;
	
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;	
	}
	
	
	@Override
	@Transactional(readOnly = true)
	public List<User> findAll() {
		return (List) this.repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<User> findById(@NonNull Long id) {
		return this.repository.findById(id);
	}

	@Override
	@Transactional()
	public User save(User user) {
		return this.repository.save(user);
	}

	@Override
	@Transactional()
	public void deleteById(Long id) {
		this.repository.deleteById(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<User> findAll(Pageable pageable) {
		return this.repository.findAll(pageable);
	}
}
