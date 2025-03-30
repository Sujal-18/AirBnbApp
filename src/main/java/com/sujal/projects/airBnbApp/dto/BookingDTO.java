package com.sujal.projects.airBnbApp.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sujal.projects.airBnbApp.entity.Guest;
import com.sujal.projects.airBnbApp.entity.Hotel;
import com.sujal.projects.airBnbApp.entity.Room;
import com.sujal.projects.airBnbApp.entity.User;
import com.sujal.projects.airBnbApp.entity.enums.BookingStatus;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDTO {

    private Long id;
    private Integer roomsCount;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private BookingStatus bookingStatus;

    private Set<GuestDTO> guests;
    private BigDecimal amount;

}
