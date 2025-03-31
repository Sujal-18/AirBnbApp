package com.sujal.projects.airBnbApp.service;

import com.sujal.projects.airBnbApp.dto.RoomDTO;
import com.sujal.projects.airBnbApp.entity.Room;

import java.util.List;

public interface RoomService {

    public RoomDTO getRoomById(Long id);
    public RoomDTO createNewRoom(Long id, RoomDTO roomDTO);
    public List<RoomDTO> getAllRoomsInHotel(Long hotelId);
    public void deleteRoomById(Long roomId);

    RoomDTO updateRoomById(Long hotelId, Long roomId, RoomDTO roomDTO);
}
