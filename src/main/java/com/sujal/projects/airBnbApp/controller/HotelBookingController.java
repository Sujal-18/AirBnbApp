package com.sujal.projects.airBnbApp.controller;

import com.sujal.projects.airBnbApp.dto.BookingDTO;
import com.sujal.projects.airBnbApp.dto.BookingRequestDTO;
import com.sujal.projects.airBnbApp.dto.GuestDTO;
import com.sujal.projects.airBnbApp.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequiredArgsConstructor
@RequestMapping("/bookings")
public class HotelBookingController {

    private final BookingService bookingService;

    @PostMapping(path = "/init")
    public ResponseEntity<BookingDTO> initialiseBooking(@RequestBody BookingRequestDTO bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping(path = "/{bookingId}/addGuests")
    public ResponseEntity<BookingDTO> addGuests(@PathVariable Long bookingId, @RequestBody List<GuestDTO> guestsDTOList){
        return ResponseEntity.ok(bookingService.addGuests(bookingId,guestsDTOList));
    }

}
