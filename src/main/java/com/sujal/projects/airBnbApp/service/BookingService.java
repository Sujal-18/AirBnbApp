package com.sujal.projects.airBnbApp.service;


import com.stripe.model.Event;
import com.sujal.projects.airBnbApp.dto.*;

import java.time.LocalDate;
import java.util.List;


public interface BookingService {


    public BookingDTO initialiseBooking(BookingRequestDTO bookingRequest);

    BookingDTO addGuests(Long bookingId, List<GuestDTO> guestsDTOList);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId);

    String getBookingStatus(Long bookingId);


    List<BookingDTO> getAllBookingByHotelId(Long hotelId);

    HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate);

    List<BookingDTO> getMyBookings();
}
