package com.mon_rec_sys.service;

import com.mon_rec_sys.dto.UserDTO;

public interface UserService {
    UserDTO createUser(UserDTO user);

    UserDTO getUser(Long userId);

    UserDTO getUserByEmail(String email);
}
