package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends CustomException {
    public AccessDeniedException() {
        super(ErrorMessages.ACCESS_DENIED, HttpStatus.FORBIDDEN);
    }
}
