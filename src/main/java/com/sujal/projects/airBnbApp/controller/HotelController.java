package com.sujal.projects.airBnbApp.controller;


import com.sujal.projects.airBnbApp.dto.HotelDTO;
import com.sujal.projects.airBnbApp.entity.Hotel;
import com.sujal.projects.airBnbApp.repository.HotelRepository;
import com.sujal.projects.airBnbApp.service.HotelService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/admin/hotels")
@Slf4j
@RequiredArgsConstructor
public class HotelController {

    public final HotelService hotelService;
    public final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<HotelDTO> createNewHotel(@RequestBody HotelDTO hotelDTO){
        log.info("Attempting to create new Hotel with name:{}",hotelDTO.getName());
        HotelDTO savedHotelDTO = hotelService.createNewHotel(hotelDTO);
        return new ResponseEntity<>(savedHotelDTO, HttpStatus.CREATED);

    }

    @GetMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Long hotelId){
        HotelDTO hotelDTO = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDTO);
    }

    @PutMapping(path ="/{hotelId}" )
    public ResponseEntity<HotelDTO> updateHotelById(@PathVariable Long hotelId,@RequestBody HotelDTO hotelDTO){
        HotelDTO savedHotel = hotelService.updateHotelById(hotelId,hotelDTO);
        return ResponseEntity.ok(savedHotel);

    }

    @DeleteMapping(path = "/{hotelId}")
    public ResponseEntity<HotelDTO> deleteHotelById(@PathVariable Long hotelId){
        HotelDTO deletedHotel = hotelService.deleteHotelById(hotelId);
        return ResponseEntity.ok(deletedHotel);
    }
    @PatchMapping(path = "/activate/{hotelId}")
    public ResponseEntity<Void> activateHotelById(@PathVariable Long hotelId){
        hotelService.activateHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }
}
