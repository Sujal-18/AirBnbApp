package com.sujal.projects.airBnbApp.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import com.sujal.projects.airBnbApp.dto.*;
import com.sujal.projects.airBnbApp.entity.*;
import com.sujal.projects.airBnbApp.entity.enums.BookingStatus;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.exception.UnAuthorizedException;
import com.sujal.projects.airBnbApp.repository.*;
import com.sujal.projects.airBnbApp.strategy.PricingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.sujal.projects.airBnbApp.util.AppUtils.getCurrentUser;

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
    private final CheckOutService checkoutService;
    private final PricingService pricingService;

    @Value("${frontend.url}")
    private  String frontendUrl;


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
//        for(Inventory inventory : inventoryList){
//            inventory.setReservedCount(inventory.getReservedCount()+bookingRequest.getRoomsCount());
//        }
//        inventoryRepository.saveAll(inventoryList);

        //Instead of running for loop same can be done using SQL
        inventoryRepository.initBooking(room.getId(),bookingRequest.getCheckInDate(),bookingRequest.getCheckOutDate(),bookingRequest.getRoomsCount());

        //Create the booking

        //TODO: Calculate dynamic amount
        BigDecimal priceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
        BigDecimal totalPrice = priceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));

        Booking booking = Booking.builder()
//                .id(1L)
                .bookingStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate())
                .checkOutDate(bookingRequest.getCheckOutDate())
                .user(getCurrentUser())
                .roomsCount(bookingRequest.getRoomsCount())
                .amount(totalPrice)  //added dynamic price calculated
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

    @Override
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with id: "+bookingId));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getId());
        }
        if(hasBookingExpired(booking)) {
            throw new IllegalStateException("Booking has already expired!");
        }

        String sessionUrl = checkoutService.getCheckoutSession(booking,frontendUrl+"/payments/success",frontendUrl+"/payments/failure");

        booking.setBookingStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepository.save(booking);
        return sessionUrl;
    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if("checkout.session.completed".equals(event.getType())){
            Session session  =(Session) event.getDataObjectDeserializer().getObject().orElse(null);
            if(session == null) return;

            String sessionId = session.getId();
            Booking booking = bookingRepository.findByPaymentSessionId(sessionId).orElseThrow(()-> new ResourceNotFoundException("Booking not found for session with ID:"+sessionId));

            booking.setBookingStatus(BookingStatus.CONFIRMED);
            bookingRepository.save(booking);

            inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

            inventoryRepository.confirmBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

            log.info("Successfully confirmed the booking for Booking ID: {} ",booking.getId());

        }
        else{
            log.warn("Unhandled event type: {}",event.getType());
        }
    }


    @Transactional
    public void cancelBooking(Long bookingId){

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with ID: "+bookingId));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getId());
        }
        if(booking.getBookingStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("Only Confirmed bookings can be cancelled");
        }

        booking.setBookingStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);

        inventoryRepository.findAndLockReservedInventory(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

        inventoryRepository.cancelBooking(booking.getRoom().getId(),booking.getCheckInDate(),booking.getCheckOutDate(),booking.getRoomsCount());

        //Handle the refund
        try{
            Session session = Session.retrieve(booking.getPaymentSessionId());
            RefundCreateParams refundParams = RefundCreateParams.builder()
                    .setPaymentIntent(session.getPaymentIntent())
                    .build();
            Refund.create(refundParams);
        }
        catch (StripeException e){
            throw new RuntimeException(e);
        }


    }

    @Override
    public String getBookingStatus(Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()-> new ResourceNotFoundException("Booking not found with ID:"+bookingId));
        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorizedException("Booking does not belong to this user with id: "+user.getId());
        }

        return booking.getBookingStatus().name();
    }



    @Override
    public List<BookingDTO> getAllBookingByHotelId(Long hotelId) {
        log.info("Fetching all bookings of hotel with ID : {}",hotelId);
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new AccessDeniedException("User is not the owner of hotel with ID: "+hotelId);
        }

        List<Booking> bookings = bookingRepository.findByHotel(hotel);
        return bookings.stream()
                .map(booking -> modelMapper.map(booking,BookingDTO.class))
                .collect(Collectors.toList());

    }

    @Override
    public HotelReportDTO getHotelReport(Long hotelId, LocalDate startDate, LocalDate endDate) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        User user = getCurrentUser();

        log.info("Generating report for hotel with ID : {}",hotelId);

        if(!user.equals(hotel.getOwner())){
            throw new AccessDeniedException("User is not the owner of hotel with ID: "+hotelId);
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Booking>  bookings = bookingRepository.findByHotelAndCreatedAtBetween(hotel,startDateTime,endDateTime);

        Long totalConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .count();

        BigDecimal totalRevenueOfConfirmedBookings = bookings.stream()
                .filter(booking -> booking.getBookingStatus() == BookingStatus.CONFIRMED)
                .map(Booking::getAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        BigDecimal averageRevenue = totalConfirmedBookings ==0 ? BigDecimal.ZERO : totalRevenueOfConfirmedBookings.divide(BigDecimal.valueOf(totalConfirmedBookings), RoundingMode.HALF_UP); //if else alternative use kela

        return new HotelReportDTO(totalConfirmedBookings,totalRevenueOfConfirmedBookings,averageRevenue);
    }

    @Override
    public List<BookingDTO> getMyBookings() {
        User user = getCurrentUser();

        List<Booking> bookings = bookingRepository.findByUser(user);
        return bookings.stream()
                .map(booking ->
                        modelMapper.map(booking,BookingDTO.class))
                .collect(Collectors.toList());

    }

    public boolean hasBookingExpired(Booking booking){
        return  booking.getCreatedAt().plusMinutes(10L).isBefore(LocalDateTime.now());
    }


}
