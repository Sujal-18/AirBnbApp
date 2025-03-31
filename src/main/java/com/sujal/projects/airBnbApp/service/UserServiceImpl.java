package com.sujal.projects.airBnbApp.service;

import com.sujal.projects.airBnbApp.dto.ProfileUpdateRequestDTO;
import com.sujal.projects.airBnbApp.dto.UserDTO;
import com.sujal.projects.airBnbApp.entity.User;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.sujal.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(()-> new ResourceNotFoundException("User not found with email: "+username));
    }
    public User getUserById(Long id){
        return userRepository.findById(id).orElseThrow(()-> new AuthenticationException("User with id: " + id + " is not found") {
        } );
    }

    @Override
    public void updateProfile(ProfileUpdateRequestDTO profileUpdateRequestDTO) {
        User user = getCurrentUser();
        log.info("Updating profile for user with ID: {}",user.getId());

        if(profileUpdateRequestDTO.getName() != null){
            user.setName(profileUpdateRequestDTO.getName());
        }
        if(profileUpdateRequestDTO.getDateOfBirth() != null) {
            user.setDateOfBirth(profileUpdateRequestDTO.getDateOfBirth());
        }
        if(profileUpdateRequestDTO.getGender() != null) {
            user.setGender(profileUpdateRequestDTO.getGender());
        }
        userRepository.save(user);
    }

    @Override
    public UserDTO getMyProfile() {
        User user = getCurrentUser();
        log.info("Getting the profile for user with id: {}",user.getId());
        return modelMapper.map(user, UserDTO.class);
    }

}
