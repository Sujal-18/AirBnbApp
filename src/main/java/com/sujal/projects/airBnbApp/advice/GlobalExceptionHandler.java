package com.sujal.projects.airBnbApp.advice;

import com.sujal.projects.airBnbApp.exception.ResourceNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationException(AuthenticationException ex){
        ApiError apiError = ApiError.builder()

                .message(ex.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtuthenticationException(JwtException e){
        ApiError apiError = ApiError.builder()
                .message(e.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatusException(ResponseStatusException ex){
        ApiError apiError = ApiError.builder()

                .message(ex.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e){
        ApiError apiError = ApiError.builder()

                .message(e.getLocalizedMessage())
                .status(HttpStatus.FORBIDDEN)
                .build();
        return new ResponseEntity<>(apiError,HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }

}














