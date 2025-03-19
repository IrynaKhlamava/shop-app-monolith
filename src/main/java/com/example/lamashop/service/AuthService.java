package com.example.lamashop.service;

import com.example.lamashop.dto.AuthResponseDto;
import com.example.lamashop.dto.LoginRequest;
import com.example.lamashop.dto.RegisterRequestDto;
import com.example.lamashop.exception.ErrorMessages;
import com.example.lamashop.model.User;
import com.example.lamashop.exception.CustomException;
import com.example.lamashop.exception.EmailAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    private final RedisTokenService redisTokenService;

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        User newUser = userService.createUser(request);

        logger.info("New user registered: {}", newUser.getEmail());

        return generateAuthTokens(newUser.getId());
    }

    public AuthResponseDto login(LoginRequest request) {

        User user = userService.validateUserCredentials(request.getEmail(), request.getPassword());

        handleOldRefreshToken(user.getId());

        return generateAuthTokens(user.getId());
    }

    private void handleOldRefreshToken(String userId) {
        String oldRefreshToken = redisTokenService.getActiveRefreshToken(userId);

        if (oldRefreshToken != null) {
            logger.info("Blacklisting old refresh token for user: {}", userId);
            redisTokenService.blacklistToken(oldRefreshToken, jwtService.getExpirationTime(oldRefreshToken));
        }
    }

    public AuthResponseDto refreshAccessToken(String refreshToken) {

        String userId = validateRefreshToken(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(userId);

        return new AuthResponseDto(newAccessToken, refreshToken);
    }

    private String validateRefreshToken(String refreshToken) {
        if (redisTokenService.isTokenBlacklisted(refreshToken)) {
            throw new CustomException(ErrorMessages.REFRESH_TOKEN_BLACKLISTED, HttpStatus.UNAUTHORIZED);
        }

        if (!redisTokenService.isRefreshTokenValid(refreshToken)) {
            throw new CustomException(ErrorMessages.REFRESH_TOKEN_INVALID, HttpStatus.UNAUTHORIZED);
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new CustomException(ErrorMessages.REFRESH_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }

        return jwtService.extractUserId(refreshToken);
    }

    public AuthResponseDto generateAuthTokens(String userId) {

        String accessToken = jwtService.generateAccessToken(userId);
        String refreshToken = jwtService.generateRefreshToken(userId);

        redisTokenService.storeRefreshToken(userId, refreshToken, jwtService.getExpirationTime(refreshToken));

        return new AuthResponseDto(accessToken, refreshToken);
    }
}
