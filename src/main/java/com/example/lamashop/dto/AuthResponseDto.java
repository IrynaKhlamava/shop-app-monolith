package com.example.lamashop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {

    private String accessToken;

    private String refreshToken;
}
