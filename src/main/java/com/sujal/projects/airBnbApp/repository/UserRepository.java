package com.sujal.projects.airBnbApp.repository;

import com.sujal.projects.airBnbApp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    public Optional<User> findByEmail(String email);
}
