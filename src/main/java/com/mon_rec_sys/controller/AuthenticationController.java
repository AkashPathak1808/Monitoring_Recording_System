package com.mon_rec_sys.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.mon_rec_sys.payload.JwtRequest;
import com.mon_rec_sys.service.impl.UserDetailsServiceImpl;
import com.mon_rec_sys.utils.JwtUtils;

@RestController
public class AuthenticationController {

	private UserDetailsServiceImpl userDetails;

	private AuthenticationManager manager;

	private JwtUtils jwtUtil;

	public AuthenticationController(UserDetailsServiceImpl userDetail, AuthenticationManager manager,
			JwtUtils jwtUtils) {
		this.userDetails = userDetail;
		this.manager = manager;
		this.jwtUtil = jwtUtils;
	}

	@PostMapping("/generate-token")
	public ResponseEntity<?> genarateToken(@RequestBody JwtRequest jwtRequest) throws Exception {
		try {
			this.manager.authenticate(
					new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
		} catch (UsernameNotFoundException e) {
			e.printStackTrace();
			throw new Exception("Bad Credentials");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Bad Credentials");
		}

		UserDetails userDetails = this.userDetails.loadUserByUsername(jwtRequest.getUsername());
		String token = this.jwtUtil.generateToken(userDetails);
		return ResponseEntity.ok(token);
	}

}
