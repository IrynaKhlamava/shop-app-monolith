package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {
    public ValidationException() {
        super(AppMessages.INVALID_PASSWORD, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
