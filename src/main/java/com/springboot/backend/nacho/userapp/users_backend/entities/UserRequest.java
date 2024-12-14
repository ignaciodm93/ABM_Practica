package com.springboot.backend.nacho.userapp.users_backend.entities;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequest implements IUser {
	
	@NotBlank
	@Column(name = "name")
	private String name;
	
	@NotBlank
	@Column(name = "lastname")
	private String lastname;
	
	@NotBlank
	@Email
	@Column(name = "email")
	private String email;

	@NotBlank
	@Size(min=4, max=12)
	@Column(name = "username")
	private String username;
	
	private Boolean admin;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Boolean getIsAdmin() {
		return admin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.admin = isAdmin;
	}

	@Override
	public boolean isAdmin() {
		return getIsAdmin();
	}
}
