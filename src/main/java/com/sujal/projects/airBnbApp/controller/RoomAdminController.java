package com.sujal.projects.airBnbApp.controller;

import com.sujal.projects.airBnbApp.dto.RoomDTO;
import com.sujal.projects.airBnbApp.repository.RoomRepository;
import com.sujal.projects.airBnbApp.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/admin/hotels/{hotelId}/rooms")
@RequiredArgsConstructor
public class RoomAdminController {
    private final RoomService roomService;
    @PostMapping
    public ResponseEntity<RoomDTO> createNewRoom(@PathVariable Long hotelId, @RequestBody RoomDTO roomDTO){
        RoomDTO savedRoomDTO = roomService.createNewRoom(hotelId,roomDTO);
        return new ResponseEntity<>(savedRoomDTO, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<RoomDTO>> getAllRoomsInHotel(@PathVariable Long hotelId){
        return ResponseEntity.ok(roomService.getAllRoomsInHotel(hotelId));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Long roomId){
        RoomDTO roomDTO = roomService.getRoomById(roomId);
        return new ResponseEntity<>(roomDTO,HttpStatus.FOUND);
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Long hotelId,@PathVariable Long roomId){
        roomService.deleteRoomById(roomId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = "/{roomId}")
    public ResponseEntity<RoomDTO> updateRoomById(@PathVariable Long hotelId,@PathVariable Long roomId,@RequestBody RoomDTO roomDTO){
        RoomDTO  savedRoomdto = roomService.updateRoomById(hotelId,roomId,roomDTO);
        return ResponseEntity.ok(savedRoomdto);
    }

}
