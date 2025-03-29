package com.sujal.projects.airBnbApp.service;

import com.sujal.projects.airBnbApp.dto.BookingDTO;
import com.sujal.projects.airBnbApp.dto.BookingRequestDTO;
import com.sujal.projects.airBnbApp.dto.GuestDTO;
import com.sujal.projects.airBnbApp.dto.HotelDTO;
import com.sujal.projects.airBnbApp.entity.*;
import com.sujal.projects.airBnbApp.entity.enums.BookingStatus;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.exception.UnAuthorizedException;
import com.sujal.projects.airBnbApp.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final ModelMapper modelMapper;
    private final BookingRepository bookingRepository;
    private  final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final InventoryRepository inventoryRepository;
    private final GuestRepository guestRepository;

    @Transactional
    public BookingDTO initialiseBooking(BookingRequestDTO bookingRequest) {
        log.info("Initialising booking for hotel: {},room: {}, date: {}-{}",bookingRequest.getHotelId(),bookingRequest.getRoomId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate());
        Hotel hotel = hotelRepository.findById(bookingRequest.getHotelId()).orElseThrow(()->new ResourceNotFoundException("Hotel not found with id:"+bookingRequest.getHotelId()));
        Room room = roomRepository.findById(bookingRequest.getRoomId()).orElseThrow(()-> new ResourceNotFoundException("Room not found with id:"+bookingRequest.getRoomId()));

        List<Inventory> inventoryList = inventoryRepository.findAndLockAvailableInventory(room.getId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate())+1;

        if(inventoryList.size() != daysCount){
            throw new IllegalStateException("Room is not available anymore");
        }

        //Reserve the room/ update the booked count of inventories
        for(Inventory inventory : inventoryList){
            inventory.setReservedCount(inventory.getReservedCount()+bookingRequest.getRoomsCount());
        }
        inventoryRepository.saveAll(inventoryList);

        //Create the booking

        //TODO: Calculate dynamic amount

        Booking booking = Booking.builder()
//                .id(1L)
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(BigDecimal.TEN)
                .build();

        Booking newBooking = bookingRepository.save(booking);

        return modelMapper.map(newBooking,BookingDTO.class);

    }

    @Override
    public BookingDTO addGuests(Long bookingId, List<GuestDTO> guestsDTOList) {

        log.info("Adding guests into booking with id:"+bookingId);
        Booking booking  = bookingRepository.findById(bookingId).orElseThrow(()->
                new ResourceNotFoundException("Booking not found with id:"+bookingId));

        User user= getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getId());
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking has already expired");
        }
        Set<Guest> guests = booking.getGuests();
        for(GuestDTO guestDTO : guestsDTOList){
            Guest guest = modelMapper.map(guestDTO,Guest.class);
            guest.setUser(getCurrentUser());
            guest = guestRepository.save(guest);
            booking.getGuests().add(guest);

        }

        booking.setBookingStatus(BookingStatus.GUESTS_ADDED);
        booking = bookingRepository.save(booking);
        return modelMapper.map(booking,BookingDTO.class);
    }

    public boolean hasBookingExpired(Booking booking){
        return  booking.getCreatedAt().plusMinutes(10L).isBefore(LocalDateTime.now());
    }

    public User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
