package com.sujal.projects.airBnbApp.service;

import com.sujal.projects.airBnbApp.entity.User;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()-> new ResourceNotFoundException("User not found with email: "+username));
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new AuthenticationException("User with id: " + id + " is not found") {
        } );
    }

}
