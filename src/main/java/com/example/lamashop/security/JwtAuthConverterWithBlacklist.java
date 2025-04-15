package com.example.lamashop.security;

import com.example.lamashop.exception.CustomException;
import com.example.lamashop.service.RedisTokenService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthConverterWithBlacklist implements Converter<Jwt, AbstractAuthenticationToken> {

    private final RedisTokenService redisTokenService;

    @Override
    public AbstractAuthenticationToken convert(@NotNull Jwt jwt) {
        String token = jwt.getTokenValue();

        if (redisTokenService.isTokenBlacklisted(token)) {
            throw new CustomException("Token is blacklisted", HttpStatus.UNAUTHORIZED);
        }

        Collection<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);
        return new UsernamePasswordAuthenticationToken(jwt.getSubject(), null, authorities);
    }

    private Collection<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("authorities");
        if (roles == null) return List.of();

        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}