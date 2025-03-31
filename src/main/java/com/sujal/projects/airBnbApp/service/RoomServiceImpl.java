package com.sujal.projects.airBnbApp.service;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.sujal.projects.airBnbApp.util.AppUtils.getCurrentUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements  RoomService{

    private final RoomRepository roomRepository;

    private final HotelRepository hotelRepository;

    private final ModelMapper modelMapper;

    private final InventoryService inventoryService;

    @Override
    public RoomDTO getRoomById(Long id) {
        log.info("Getting room with id {}",id);
       Room room  = roomRepository.findById(id)
               .orElseThrow(()-> new ResourceNotFoundException("Room not found with ID: "+id));
       return modelMapper.map(room, RoomDTO.class);
    }

    public RoomDTO createNewRoom(Long hotelId, RoomDTO roomDto) {
        log.info("Creating a new room in hotel with ID: {}", hotelId);
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with ID: "+hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner().getUsername())){
            throw new UnAuthorizedException("This user does not own this hotel with id: "+hotelId);
        }
        Room room = modelMapper.map(roomDto, Room.class);
        room.setHotel(hotel);
        room = roomRepository.save(room);
        if(hotel.getActive()){
            inventoryService.initializeRoomForAYear(room);
        }
        return modelMapper.map(room, RoomDTO.class);
    }

    @Override
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()->new ResourceNotFoundException("Hotel with ID: "+hotelId+" is not found"));
        List<Room> rooms =  hotel.getRooms();
        return rooms.stream()
                .map(room ->
                        modelMapper.map(room, RoomDTO.class)
                        ).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting room with ID:",roomId);
        Room room  = roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found with ID: "+roomId));

        Hotel hotel = room.getHotel();

        // Delete all future inventory for this room only by owner
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!user.equals(hotel.getOwner().getUsername())){
            throw new UnAuthorizedException("This user does not own this hotel with id: "+hotel.getId());
        }
        inventoryService.deleteAllInventories(room);
        roomRepository.deleteById(roomId);


    }

    @Override
    public RoomDTO updateRoomById(Long hotelId, Long roomId, RoomDTO roomDTO) {
        log.info("Updating the room with ID: {} of hotel with ID: {}",roomId,hotelId);

        Hotel hotel = hotelRepository.findById(hotelId).orElseThrow(()-> new ResourceNotFoundException("Hotel not found with id: "+hotelId));

        User user = getCurrentUser();
        if(!user.equals(hotel.getOwner())){
            throw new UnAuthorizedException("The user does not own this hotel with id:"+hotelId);
        }

        Room room = roomRepository.findById(roomId).orElseThrow(()-> new ResourceNotFoundException("Room not found with id: "+roomId));

        Room toBeSavedRoom = modelMapper.map(roomDTO, Room.class);
        toBeSavedRoom.setId(roomId);
        toBeSavedRoom.setHotel(hotel);
        toBeSavedRoom = roomRepository.save(toBeSavedRoom);
        return modelMapper.map(toBeSavedRoom,RoomDTO.class);


    }
}
