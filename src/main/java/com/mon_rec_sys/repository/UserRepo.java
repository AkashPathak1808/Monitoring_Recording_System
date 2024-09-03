package com.mon_rec_sys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mon_rec_sys.entity.User;

public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
