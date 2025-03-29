package com.sujal.projects.airBnbApp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
@AllArgsConstructor
public class HotelInfoDTO {

    private HotelDTO hotel;
    private List<RoomDTO> rooms;
}
