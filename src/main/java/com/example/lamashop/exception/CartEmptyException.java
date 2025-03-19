package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class CartEmptyException extends CustomException {
    public CartEmptyException() {
        super(ErrorMessages.CART_IS_EMPTY, HttpStatus.BAD_REQUEST);
    }
}
