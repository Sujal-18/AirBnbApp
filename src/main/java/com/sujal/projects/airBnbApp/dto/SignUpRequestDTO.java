package com.sujal.projects.airBnbApp.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class SignUpRequestDTO {
    private String name;
    private String email;
    private String password;
}
