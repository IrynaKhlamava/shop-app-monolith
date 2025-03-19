package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {
    public ValidationException() {
        super(ErrorMessages.INVALID_PASSWORD, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
