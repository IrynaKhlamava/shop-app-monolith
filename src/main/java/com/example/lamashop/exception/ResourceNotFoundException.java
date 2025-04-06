package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

import static com.example.lamashop.exception.AppMessages.NOT_FOUND;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String resourceName) {
        super(String.format(NOT_FOUND, resourceName), HttpStatus.NOT_FOUND);
    }

    public static ResourceNotFoundException forProduct() {
        return new ResourceNotFoundException(AppMessages.PRODUCT_NOT_FOUND);
    }

    public static ResourceNotFoundException forUser() {
        return new ResourceNotFoundException(AppMessages.USER_NOT_FOUND);
    }
}
