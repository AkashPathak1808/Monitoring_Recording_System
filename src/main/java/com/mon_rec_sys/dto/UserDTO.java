package com.mon_rec_sys.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String name;
    private String email;
    private String password;
}
