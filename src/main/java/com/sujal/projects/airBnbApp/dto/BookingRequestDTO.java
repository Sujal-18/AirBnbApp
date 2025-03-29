package com.sujal.projects.airBnbApp.dto;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private Long hotelId;
    private Long roomId;

    private LocalDate checkInDate;

    private LocalDate checkOutDate;

    private Integer roomsCount;
}
