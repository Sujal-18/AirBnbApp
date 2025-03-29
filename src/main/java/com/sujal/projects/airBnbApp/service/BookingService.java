package com.sujal.projects.airBnbApp.service;


import com.sujal.projects.airBnbApp.dto.BookingDTO;
import com.sujal.projects.airBnbApp.dto.BookingRequestDTO;
import com.sujal.projects.airBnbApp.dto.GuestDTO;

import java.util.List;


public interface BookingService {


    public BookingDTO initialiseBooking(BookingRequestDTO bookingRequest);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestsDTOList);
}
