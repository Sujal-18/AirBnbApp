package com.sujal.projects.airBnbApp.controller;

import com.sujal.projects.airBnbApp.dto.HotelDTO;
import com.sujal.projects.airBnbApp.dto.HotelInfoDTO;
import com.sujal.projects.airBnbApp.dto.HotelPriceDTO;
import com.sujal.projects.airBnbApp.dto.HotelSearchRequestDTO;
import com.sujal.projects.airBnbApp.service.HotelService;
import com.sujal.projects.airBnbApp.service.InventoryService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/hotels")
public class HotelBrowseController {
    private final InventoryService inventoryService;
    private  final HotelService hotelService;

    @GetMapping(path = "/search")
    public ResponseEntity<Page<HotelPriceDTO>> searchHotels(@RequestBody HotelSearchRequestDTO hotelSearchRequestDTO){

        Page<HotelPriceDTO> page = inventoryService.searchHotels(hotelSearchRequestDTO);
        return ResponseEntity.ok(page);
    }

    @GetMapping(path = "/{hotelId}/info")
    public ResponseEntity<HotelInfoDTO> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }
}
