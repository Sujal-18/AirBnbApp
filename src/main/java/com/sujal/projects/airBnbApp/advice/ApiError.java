package com.sujal.projects.airBnbApp.advice;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private HttpStatus status;
    private String message;
    private List<String> subErrors;

}
