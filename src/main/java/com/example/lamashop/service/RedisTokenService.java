package com.example.lamashop.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class RedisTokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String WHITE_LIST_PREFIX = "whitelist:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    private static final Logger logger = LoggerFactory.getLogger(RedisTokenService.class);

    public void storeRefreshToken(String userId, String refreshToken, long expirationMillis) {
        logger.debug("Saving refresh token: {}", WHITE_LIST_PREFIX + refreshToken);
        redisTemplate.opsForValue().set(WHITE_LIST_PREFIX + refreshToken, userId, Duration.ofMillis(expirationMillis));
    }

    public void blacklistToken(String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + refreshToken, "blacklisted", Duration.ofMillis(expirationMillis));
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        logger.debug("Checking refresh token in Redis: {}", WHITE_LIST_PREFIX + refreshToken);
        return Boolean.TRUE.equals(redisTemplate.hasKey(WHITE_LIST_PREFIX + refreshToken));
    }

    public boolean isTokenBlacklisted(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + refreshToken));
    }

    public String getActiveRefreshToken(String userId) {
        Set<String> keys = redisTemplate.keys(WHITE_LIST_PREFIX + "*");

        for (String key : keys) {
            Object storedUserId = redisTemplate.opsForValue().get(key);
            if (userId.equals(String.valueOf(storedUserId))) {
                return key.replace(WHITE_LIST_PREFIX, "");
            }
        }
        return null;
    }

    public void removeFromWhitelist(String oldRefreshToken) {
        redisTemplate.delete(WHITE_LIST_PREFIX + oldRefreshToken);
    }
}