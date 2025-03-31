package com.sujal.projects.airBnbApp.dto;


import com.sujal.projects.airBnbApp.entity.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ProfileUpdateRequestDTO {

    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
}
