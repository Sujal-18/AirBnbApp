package com.sujal.projects.airBnbApp.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private  Long id;
    private String email;

    private String name;
}
