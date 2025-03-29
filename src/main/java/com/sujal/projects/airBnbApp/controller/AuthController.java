package com.sujal.projects.airBnbApp.controller;

import com.sujal.projects.airBnbApp.dto.LoginDTO;
import com.sujal.projects.airBnbApp.dto.LoginResponseDTO;
import com.sujal.projects.airBnbApp.dto.SignUpRequestDTO;
import com.sujal.projects.airBnbApp.dto.UserDTO;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignUpRequestDTO signUpRequestDTO){
        UserDTO userDTO = authService.signUp(signUpRequestDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response){

        String[] tokens = authService.login(loginDTO);
        Cookie cookie = new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDTO(tokens[0]));
    }
    @PostMapping(path = "/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElseThrow(()-> new AuthenticationServiceException("Refresh Token not found inside the cookies"));

        String accessToken=authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDTO(accessToken));




    }

}
