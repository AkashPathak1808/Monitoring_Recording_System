package com.mon_rec_sys.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mon_rec_sys.entity.User;
import com.mon_rec_sys.exception.ResourceNotFoundException;
import com.mon_rec_sys.payload.CustomUserDetails;
import com.mon_rec_sys.repository.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	private UserRepo userRepo;
	
	public UserDetailsServiceImpl(UserRepo userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.userRepo.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User", "email", username));
		return new CustomUserDetails(user);
	}

}
