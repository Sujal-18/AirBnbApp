package com.sujal.projects.airBnbApp.dto;


import com.sujal.projects.airBnbApp.entity.User;
import com.sujal.projects.airBnbApp.entity.enums.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuestDTO {
        private Long id;
        private User user;
        private String name;
        private Gender gender;
        private Integer age;
}
