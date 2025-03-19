package com.example.lamashop.filter;

import com.example.lamashop.service.JwtService;
import com.example.lamashop.service.RedisTokenService;
import com.example.lamashop.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;

    private final RedisTokenService redisTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            logger.info("Extracted token: {}", token);

            if (redisTokenService.isTokenBlacklisted(token)) {
                logger.warn("Token is blacklisted: {}", token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Token is blacklisted");
                return;
            }

            try {
                if (jwtService.validateToken(token)) {
                    String userId = jwtService.extractUserId(token);
                    logger.info("User ID from token: {}", userId);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userId, null, null);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    logger.info("User authenticated successfully: {}", userId);
                }
            } catch (CustomException e) {
                logger.warn("Authentication failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                return;
            }
        } else {
            logger.warn("No valid Authorization header found");
        }

        filterChain.doFilter(request, response);
    }
}
