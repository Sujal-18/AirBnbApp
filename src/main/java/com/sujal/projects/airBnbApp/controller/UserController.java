package com.sujal.projects.airBnbApp.controller;

import com.sujal.projects.airBnbApp.dto.BookingDTO;
import com.sujal.projects.airBnbApp.dto.ProfileUpdateRequestDTO;
import com.sujal.projects.airBnbApp.dto.UserDTO;
import com.sujal.projects.airBnbApp.entity.Booking;
import com.sujal.projects.airBnbApp.service.BookingService;
import com.sujal.projects.airBnbApp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;

    @PatchMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody ProfileUpdateRequestDTO profileUpdateRequestDTO){
        userService.updateProfile(profileUpdateRequestDTO);
        return ResponseEntity.noContent().build();
    }
    @GetMapping(path = "/myBookings")
    public ResponseEntity<List<BookingDTO>> getMyBookings(){
        return ResponseEntity.ok(bookingService.getMyBookings());
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getMyProfile(){
        UserDTO userDTO = userService.getMyProfile();
        return ResponseEntity.ok(userDTO);
    }

}
