package com.example.lamashop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDto {

    private String email;

    private String password;

    private String firstName;

    private String lastName;
}