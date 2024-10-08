package com.mon_rec_sys.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mon_rec_sys.dto.UserDTO;
import com.mon_rec_sys.entity.User;
import com.mon_rec_sys.exception.ResourceNotFoundException;
import com.mon_rec_sys.repository.UserRepo;
import com.mon_rec_sys.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private UserRepo userRepo;

    private ModelMapper modelMapper;
    
    private PasswordEncoder encoder;

    public UserServiceImpl(UserRepo repo, ModelMapper mapper, PasswordEncoder encoder){
        this.userRepo = repo;
        this.modelMapper = mapper;
        this.encoder = encoder;
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        User mapped = this.modelMapper.map(user, User.class);
        String encode = this.encoder.encode(mapped.getPassword());
        mapped.setPassword(encode);
        User savedUser = this.userRepo.save(mapped);
        UserDTO userDTO = this.modelMapper.map(savedUser, UserDTO.class);
        return userDTO;
    }

    @Override
    public UserDTO getUser(Long userId) {
        User user = this.userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "User id", userId));
        
        UserDTO userDTO = this.modelMapper.map(user, UserDTO.class);
        return userDTO;
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = this.userRepo.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        UserDTO userDTO = this.modelMapper.map(user, UserDTO.class);
        return userDTO;
    }
}
