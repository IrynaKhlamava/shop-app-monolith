package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class NotEnoughStockException extends CustomException {

    public NotEnoughStockException() {
        super(AppMessages.NOT_ENOUGH_STOCK, HttpStatus.BAD_REQUEST);
    }
}
