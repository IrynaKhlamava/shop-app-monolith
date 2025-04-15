package com.example.lamashop.exception;

import org.springframework.http.HttpStatus;

import static com.example.lamashop.exception.AppMessages.USER_NOT_FOUND_BY_EMAIL;
import static com.example.lamashop.exception.AppMessages.USER_NOT_FOUND_BY_ID;

public class UserNotFoundException extends CustomException {

    public static UserNotFoundException byId(String id) {
        return new UserNotFoundException(String.format(USER_NOT_FOUND_BY_ID, id));
    }

    public static UserNotFoundException byEmail(String email) {
        return new UserNotFoundException(String.format(USER_NOT_FOUND_BY_EMAIL, email));
    }

    private UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
