package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends CustomException {
    public EmailAlreadyExistsException() {
        super(ErrorMessages.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
    }
}