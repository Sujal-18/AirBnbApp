package com.sujal.projects.airBnbApp.service;


import com.sujal.projects.airBnbApp.dto.ProfileUpdateRequestDTO;
import com.sujal.projects.airBnbApp.dto.UserDTO;
import com.sujal.projects.airBnbApp.entity.User;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    public User getUserById(Long id);

    void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO);

    UserDTO getMyProfile();
}
