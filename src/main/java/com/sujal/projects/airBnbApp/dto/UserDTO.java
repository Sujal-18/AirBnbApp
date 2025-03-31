package com.sujal.projects.airBnbApp.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import com.sujal.projects.airBnbApp.entity.enums.Gender;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private  Long id;
    private String name;
    private String email;
    private LocalDate dateOfBirth;
    private Gender gender;
}
