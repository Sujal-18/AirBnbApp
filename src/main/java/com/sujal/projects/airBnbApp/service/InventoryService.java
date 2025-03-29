package com.sujal.projects.airBnbApp.service;

import com.sujal.projects.airBnbApp.dto.HotelDTO;
import com.sujal.projects.airBnbApp.dto.HotelPriceDTO;
import com.sujal.projects.airBnbApp.dto.HotelSearchRequestDTO;
import com.sujal.projects.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService {

    void initializeRoomForAYear(Room room);

    void deleteAllInventories(Room room);

    Page<HotelPriceDTO> searchHotels(HotelSearchRequestDTO hotelSearchRequestDTO);
}
