package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class OrderNumberGenerationException extends CustomException {
    public OrderNumberGenerationException() {
        super(AppMessages.ORDER_NUMBER_GENERATION_FAILED, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
