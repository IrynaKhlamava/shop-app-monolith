package com.example.lamashop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private String error;
    private String message;
    private int status;
    private LocalDateTime timestamp;

    public static ErrorResponse fromException(String error, String message, HttpStatus status) {
        return new ErrorResponse(error, message, status.value(), LocalDateTime.now());
    }
}
