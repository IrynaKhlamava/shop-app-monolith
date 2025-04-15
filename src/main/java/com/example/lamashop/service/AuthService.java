package com.example.lamashop.service;

import com.example.lamashop.dto.AuthResponseDto;
import com.example.lamashop.dto.LoginRequest;
import com.example.lamashop.dto.RegisterRequestDto;
import com.example.lamashop.exception.AppMessages;
import com.example.lamashop.model.User;
import com.example.lamashop.exception.CustomException;
import com.example.lamashop.exception.EmailAlreadyExistsException;
import com.example.lamashop.model.enumType.RoleName;
import com.mongodb.DuplicateKeyException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.example.lamashop.exception.AppMessages.BEARER_PREFIX;
import static com.example.lamashop.exception.AppMessages.INVALID_OR_EXPIRED_TOKEN;
import static com.example.lamashop.exception.AppMessages.LOGOUT_SUCCESS;
import static com.example.lamashop.exception.AppMessages.NO_TOKEN_PROVIDED;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponseDto register(RegisterRequestDto request) {
        try {
            User newUser = userService.createUser(request);
            logger.info("New user registered: {}", newUser.getEmail());
            return createAuthResponse(newUser);
        } catch (DuplicateKeyException e) {
            logger.warn("Attempt to register with existing email: {}", request.getEmail());
            throw new EmailAlreadyExistsException();
        }
    }

    public AuthResponseDto login(LoginRequest request) {
        User user = userService.validateUserCredentials(request.getEmail(), request.getPassword());

        return createAuthResponse(user);
    }

    public AuthResponseDto refreshAccessToken(String refreshToken) {
        String extractedRefreshToken = extractToken(refreshToken);
        String userId = validateRefreshToken(extractedRefreshToken);

        RoleName role = userService.getUserRole(userId);
        String newAccessToken = jwtService.generateAccessToken(userId, role);

        return new AuthResponseDto(newAccessToken, extractedRefreshToken, userId);
    }

    private String validateRefreshToken(String refreshToken) {
        if (redisTokenService.isTokenBlacklisted(refreshToken)) {
            throw new CustomException(AppMessages.REFRESH_TOKEN_BLACKLISTED, HttpStatus.UNAUTHORIZED);
        }

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new CustomException(AppMessages.REFRESH_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        }

        return jwtService.extractUserId(refreshToken);
    }

    public String extractToken(String header) {
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(7);
        }
        return header;
    }

    public AuthResponseDto createAuthResponse(User user) {
        String userId = user.getId();
        RoleName userRole = user.getRole();

        String accessToken = jwtService.generateAccessToken(userId, userRole);
        String refreshToken = jwtService.generateRefreshToken(userId, userRole);

        return new AuthResponseDto(accessToken, refreshToken, user.getId());
    }

    public String logout(HttpServletRequest request) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(7);

            if (jwtService.validateToken(token)) {
                long expiration = jwtService.getExpirationTime(token);
                redisTokenService.blacklistToken(token, expiration);

                logger.info("Access token blacklisted");
                return LOGOUT_SUCCESS;
            } else {
                logger.warn("Logout called with invalid token");
                return INVALID_OR_EXPIRED_TOKEN;
            }
        } else {
            logger.warn("No Authorization header provided on logout");
            return NO_TOKEN_PROVIDED;
        }
    }
}