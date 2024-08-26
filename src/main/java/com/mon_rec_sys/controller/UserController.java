package com.mon_rec_sys.controller;

import com.mon_rec_sys.dto.UserDTO;
import com.mon_rec_sys.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService service;

    public UserController(UserService service){
        this.service = service;
    }

//    create new user
    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDto){
        UserDTO userDTO = this.service.createUser(userDto);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

//    get user by index value
    @GetMapping("/get/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id){
        UserDTO userDTO = this.service.getUser(id);
        if(userDTO != null){
            return new ResponseEntity<>(userDTO, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.FOUND);
    }

    @GetMapping("/getByEmail/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email){
        UserDTO userByEmail = this.service.getUserByEmail(email);
        if(userByEmail != null){
            return new ResponseEntity<>(userByEmail, HttpStatus.FOUND);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
