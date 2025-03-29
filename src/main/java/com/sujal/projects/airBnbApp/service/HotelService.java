package com.sujal.projects.airBnbApp.service;

import com.sujal.projects.airBnbApp.dto.HotelDTO;
import com.sujal.projects.airBnbApp.dto.HotelInfoDTO;
import com.sujal.projects.airBnbApp.entity.Hotel;
import org.springframework.stereotype.Service;


public interface HotelService {

    HotelDTO createNewHotel(HotelDTO hotelDTO);

    HotelDTO getHotelById(Long id);


    HotelDTO updateHotelById(Long id, HotelDTO hotelDTO);

    HotelDTO deleteHotelById(Long hotelId);

    void activateHotelById(Long hotelId);

    HotelInfoDTO getHotelInfoById(Long hotelId);
}
