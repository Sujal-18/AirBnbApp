package com.sujal.projects.airBnbApp.security;

import com.sujal.projects.airBnbApp.dto.LoginDTO;
import com.sujal.projects.airBnbApp.dto.SignUpRequestDTO;
import com.sujal.projects.airBnbApp.dto.UserDTO;
import com.sujal.projects.airBnbApp.entity.User;
import com.sujal.projects.airBnbApp.entity.enums.Role;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Getter
@Setter

public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private  final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UserDTO signUp(SignUpRequestDTO signUpRequestDTO){

        User user = userRepository.findByEmail(signUpRequestDTO.getEmail()).orElse(null);

        if(user != null){
            throw new BadCredentialsException("User already exists with email:"+signUpRequestDTO.getEmail());
        }
        User toBeCreatedUser = modelMapper.map(signUpRequestDTO,User.class);
        toBeCreatedUser.setRoles(Set.of(Role.GUEST));
        toBeCreatedUser.setPassword(passwordEncoder.encode(signUpRequestDTO.getPassword()));
        toBeCreatedUser = userRepository.save(toBeCreatedUser);
        return modelMapper.map(toBeCreatedUser,UserDTO.class);

    }
    public String[] login(LoginDTO loginDTO){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDTO.getEmail(),loginDTO.getPassword()
        ));

        User user = (User) authentication.getPrincipal();
        String[] arr = new String[2];

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        arr[0] = accessToken;
        arr[1]= refreshToken;

        return arr;
    }

    public String refreshToken(String refreshToken) {
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User not found with id:"+id));

        String accessToken = jwtService.generateAccessToken(user);
        return accessToken;
    }

    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
