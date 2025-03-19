package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String resourceName) {
        super(resourceName + " not found", HttpStatus.NOT_FOUND);
    }

    public static ResourceNotFoundException forProduct() {
        return new ResourceNotFoundException(ErrorMessages.PRODUCT_NOT_FOUND);
    }

    public static ResourceNotFoundException forUser() {
        return new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND);
    }
}
