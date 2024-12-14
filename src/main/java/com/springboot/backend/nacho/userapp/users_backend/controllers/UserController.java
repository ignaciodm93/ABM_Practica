package com.springboot.backend.nacho.userapp.users_backend.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.backend.nacho.userapp.users_backend.entities.User;
import com.springboot.backend.nacho.userapp.users_backend.entities.UserRequest;
import com.springboot.backend.nacho.userapp.users_backend.services.UserService;

import jakarta.validation.Valid;

@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private UserService service;
	
	@GetMapping
	public List<User> list() {
		return service.findAll();
	}
	
	@GetMapping("/page/{pageNumber}")
	public Page<User> findAll(@PathVariable Integer pageNumber) {
		Pageable pageable = PageRequest.of(pageNumber, 2);
		return service.findAll(pageable);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> showById(@PathVariable Long id) {
		Optional<User> userOptional = service.findById(id);
		if (userOptional.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(userOptional.orElseThrow());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "El usuario no se encontro por el id " + id));
		}
	}
	
	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody User user, BindingResult result) {
		if (result.hasErrors()) {
			return errorChecking(result);
		}
		return ResponseEntity.status(HttpStatus.CREATED).body(service.save(user));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody UserRequest user, BindingResult result, @PathVariable Long id) {
		
		if (result.hasErrors()) {
			return errorChecking(result);
		}
		
		Optional<User> userOptional = service.update(user, id);
		if (userOptional.isPresent()) {
			return ResponseEntity.ok(userOptional.orElseThrow());
		}
		return ResponseEntity.notFound().build();
	}

	private ResponseEntity<?> errorChecking(BindingResult result) {
		Map<String, String> errors = new HashMap<>();
		result.getFieldErrors().forEach(e -> {
			errors.put(e.getField(), "El campo " + e.getField() + e.getDefaultMessage());
		});
		return ResponseEntity.badRequest().body(errors);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Optional<User> userOptional = service.findById(id);
		if (userOptional.isPresent()) {
			service.deleteById(id);
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}
