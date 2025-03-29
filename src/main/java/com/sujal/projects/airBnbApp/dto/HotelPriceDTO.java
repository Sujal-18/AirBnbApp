package com.sujal.projects.airBnbApp.dto;


import com.sujal.projects.airBnbApp.entity.Hotel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class HotelPriceDTO {

    private Hotel hotel;
    private Double price;

}
