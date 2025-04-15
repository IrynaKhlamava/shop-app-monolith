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

    private static final String BLACKLIST_PREFIX = "blacklist:";

    private static final Logger logger = LoggerFactory.getLogger(RedisTokenService.class);

    public void blacklistToken(String token, long expirationMillis) {
        logger.info("Blacklisting token: {}", token);
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "blacklisted", Duration.ofMillis(expirationMillis));
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }
}