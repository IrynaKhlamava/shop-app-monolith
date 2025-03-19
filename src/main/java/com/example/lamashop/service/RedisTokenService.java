package com.example.lamashop.service;

import lombok.RequiredArgsConstructor;
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

    public void storeRefreshToken(String userId, String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(WHITE_LIST_PREFIX + refreshToken, userId, Duration.ofMillis(expirationMillis));
    }

    public void blacklistToken(String refreshToken, long expirationMillis) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + refreshToken, "blacklisted", Duration.ofMillis(expirationMillis));
    }

    public boolean isRefreshTokenValid(String refreshToken) {
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
}