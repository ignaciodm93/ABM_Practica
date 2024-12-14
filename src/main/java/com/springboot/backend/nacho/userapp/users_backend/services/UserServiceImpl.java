package com.springboot.backend.nacho.userapp.users_backend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot.backend.nacho.userapp.users_backend.entities.IUser;
import com.springboot.backend.nacho.userapp.users_backend.entities.Role;
import com.springboot.backend.nacho.userapp.users_backend.entities.User;
import com.springboot.backend.nacho.userapp.users_backend.entities.UserRequest;
import com.springboot.backend.nacho.userapp.users_backend.repositories.RoleRepository;
import com.springboot.backend.nacho.userapp.users_backend.repositories.UserRepository;

import io.micrometer.common.lang.NonNull;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository repository;
	
	private PasswordEncoder passwordEncoder;
	
	private RoleRepository roleRepository;
	
	public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
		this.repository = repository;	
		this.passwordEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
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
		user.setRoles(getRoles(user));
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		return this.repository.save(user);
	}

	private List<Role> getRoles(IUser user) {
		List<Role> roles = new ArrayList<>();
		Optional<Role> optionalRole = roleRepository.findByName("ROLE_USER");
		optionalRole.ifPresent(roles::add);
		if (user.isAdmin()) {
			Optional<Role> optionalRoleAdmin = roleRepository.findByName("ROLE_ADMIN");
			optionalRoleAdmin.ifPresent(roles::add);			
		}
		return roles;
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


	@Override
	@Transactional
	public Optional<User> update(UserRequest user, Long id) {
		Optional<User> userOptional = repository.findById(id);
		if (userOptional.isPresent()) {
			User userDb = userOptional.get();
			userDb.setEmail(user.getEmail());
			userDb.setLastname(user.getLastname());
			userDb.setName(user.getName());
			userDb.setUsername(user.getUsername());
			userDb.setRoles(getRoles(user));
			return Optional.of(repository.save(userDb));
		}
		return Optional.empty();
	}
}
