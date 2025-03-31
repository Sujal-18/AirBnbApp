package com.sujal.projects.airBnbApp.service;


import com.sujal.projects.airBnbApp.dto.HotelDTO;
import com.sujal.projects.airBnbApp.dto.HotelInfoDTO;
import com.sujal.projects.airBnbApp.dto.RoomDTO;
import com.sujal.projects.airBnbApp.entity.Hotel;
import com.sujal.projects.airBnbApp.entity.Room;
import com.sujal.projects.airBnbApp.entity.User;
import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import com.sujal.projects.airBnbApp.exception.UnAuthorizedException;
import com.sujal.projects.airBnbApp.repository.HotelRepository;
import com.sujal.projects.airBnbApp.repository.RoomRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.sujal.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {


    private final HotelRepository hotelRepository;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepository roomRepository;
    @Override
    public HotelDTO createNewHotel(HotelDTO hotelDTO){
        log.info("Creating a new Hotel with name:{}",hotelDTO.getName());
        Hotel hotel = modelMapper.map(hotelDTO,Hotel.class);
        hotel.setActive(false);
        //Set owner of the hotel which cannot be null
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hotel.setOwner(user);

        hotel = hotelRepository.save(hotel);
        log.info("Created a new Hotel with ID:{}",hotel.getId());
        return modelMapper.map(hotel,HotelDTO.class);
    }
    @Override
    public HotelDTO getHotelById(Long id){
        log.info("Getting the hotel by Id:{}",id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel with id {} not found"+id));

        //Only owner of hotel can get hotel by id
        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorizedException("This user does not own this hotel with id: "+id);
        }

        return modelMapper.map(hotel,HotelDTO.class);
    }

    @Override
    public HotelDTO updateHotelById(Long id, HotelDTO hotelDTO){
        log.info("Updating Hotel by id:{}",id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel with id "+id+"is not found"));
         modelMapper.map(hotelDTO,hotel);

        //Only owner of hotel can get update hotel by id
         User user =getCurrentUser();
         if(!user.equals(hotel.getOwner())){
             throw new UnAuthorizedException("This user does not own this hotel with id: "+id);
         }
         hotel.setId(id);
         hotel = hotelRepository.save(hotel);
         return modelMapper.map(hotel,HotelDTO.class);
    }


    @Override
    @Transactional
    public HotelDTO deleteHotelById(Long id) {
        log.info("Deleting Hotel with Id:",id);
        Hotel hotelEntity = hotelRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel is not found with id: "+id));

        //Delete future inventory for all rooms of specific hotel only
        for(Room room:hotelEntity.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepository.deleteById(room.getId());
        }
        hotelRepository.deleteById(id);
        return modelMapper.map(hotelEntity,HotelDTO.class);
    }

    @Override
    @Transactional
    public void activateHotelById(Long id){
        log.info("Activating Hotel with Id:{}",id);
        Hotel hotel = hotelRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Hotel not found with id:"+id));
        hotel.setActive(true);

        //Assuming only do it once
        for(Room room: hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }
    }

    @Override
    public HotelInfoDTO getHotelInfoById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id:"+hotelId));

        List<RoomDTO> rooms = hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, RoomDTO.class))
                .toList();

        return new HotelInfoDTO(modelMapper.map(hotel,HotelDTO.class),rooms);
    }

    @Override
    public List<HotelDTO> getAllHotels() {
        User user = getCurrentUser();
        log.info("Getting all hotels for the user with ID: {}", user.getId());
        List<Hotel> hotels = hotelRepository.findByOwner(user);
        return hotels.stream()
                .map(hotel -> modelMapper.map(hotel,HotelDTO.class))
                .collect(Collectors.toList());
    }

}
