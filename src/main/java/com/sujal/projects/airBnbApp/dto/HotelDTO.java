package com.sujal.projects.airBnbApp.dto;

import com.sujal.projects.airBnbApp.entity.HotelContactInfo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDTO {

    private Long id;

    private String name;

    private String city;
    private String[] photos;
    private String[] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;

}
