package com.example.lamashop.service;

import com.example.lamashop.exception.CustomException;
import com.example.lamashop.exception.ErrorMessages;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKeyBase64;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private SecretKey getSigningKey() {
        logger.debug("Using JWT_SECRET (Base64 Encoded): {}", secretKeyBase64);
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(String userId) {
        return generateToken(userId, accessTokenExpiration);
    }

    public String generateRefreshToken(String userId) {
        return generateToken(userId, refreshTokenExpiration);
    }

    private String generateToken(String userId, long expirationMs) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            logger.info("Validating token: {}", token);

            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            logger.info("Token is valid");
            return true;
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorMessages.TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorMessages.INVALID_TOKEN, HttpStatus.UNAUTHORIZED);
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            Date expiration = claims.getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public long getExpirationTime(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().getTime() - System.currentTimeMillis();
        } catch (ExpiredJwtException e) {
            return 0;
        }
    }
}
