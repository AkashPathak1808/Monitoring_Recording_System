package com.mon_rec_sys.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mon_rec_sys.dto.UserDTO;
import com.mon_rec_sys.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

	private UserService service;

	public UserController(UserService service) {
		this.service = service;
	}

//  create new user
	@PostMapping("/create")
	public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto) {
		UserDTO userDTO = this.service.createUser(userDto);
		return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
	}

//  get user by index value
	@GetMapping("/get/{id}")
	public ResponseEntity<?> getUser(@PathVariable Long id) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		UserDTO userDTO = this.service.getUser(id);
		if (userDTO != null && name.equals(userDTO.getEmail())) {
			return new ResponseEntity<>(userDTO, HttpStatus.FOUND);
		}
		return new ResponseEntity<>("User Not Logged In!!", HttpStatus.NOT_FOUND);
	}

//	get user by email id
	@GetMapping("/getByEmail/{email}")
	public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		UserDTO userByEmail = this.service.getUserByEmail(email);
		if (userByEmail != null && name.equals(userByEmail.getEmail())) {
			return new ResponseEntity<>(userByEmail, HttpStatus.FOUND);
		}
		return new ResponseEntity<>("User Not Logged In!!", HttpStatus.NOT_FOUND);

	}
}
