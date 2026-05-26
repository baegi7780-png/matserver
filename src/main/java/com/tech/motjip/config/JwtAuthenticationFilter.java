package com.tech.motjip.config;

import com.tech.motjip.util.JwtTokenProvider;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.util.AntPathMatcher;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final AntPathMatcher pathMatcher =
            new AntPathMatcher();

    private static final List<String> EXCLUDE_URLS =
            Arrays.asList(
                    "/",
                    "/error",

                    "/uploads/**",

                    "/fcm/test",

                    "/oauth/callback/kakao",
                    "/oauth/callback/google",

                    "/login/oauth2/code/kakao",
                    "/login/oauth2/code/google",

                    "/api/v1/auth/kakao",
                    "/api/v1/auth/refresh"
            );

    @Override
    protected boolean shouldNotFilter(
            HttpServletRequest request
    ) {

        String path =
                request.getRequestURI();

        return EXCLUDE_URLS.stream()
                .anyMatch(excludeUrl ->
                        pathMatcher.match(
                                excludeUrl,
                                path
                        )
                );
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String bearerToken =
                request.getHeader(
                        "Authorization"
                );

        log.info(
                "Authorization Header: {}",
                bearerToken
        );

        if (bearerToken == null
                || !bearerToken.startsWith("Bearer ")) {

            filterChain.doFilter(
                    request,
                    response
            );

            return;
        }

        String token =
                bearerToken.substring(7);

        try {

            if (!jwtTokenProvider.isAccessToken(
                    token
            )) {

                log.warn(
                        "Access Token이 아님 (Refresh Token 사용 시도)"
                );

                response.setStatus(
                        HttpStatus.UNAUTHORIZED.value()
                );

                response.getWriter()
                        .write(
                                "Invalid token type"
                        );

                return;
            }

            Authentication auth =
                    jwtTokenProvider.getAuthentication(
                            token
                    );

            log.info(
                    "Authentication: {}",
                    auth
            );

            log.info(
                    "Principal: {}",
                    auth.getPrincipal()
            );

            log.info(
                    "Name: {}",
                    auth.getName()
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(
                            auth
                    );

        } catch (ExpiredJwtException e) {

            log.warn(
                    "만료된 토큰"
            );

            response.setStatus(
                    HttpStatus.UNAUTHORIZED.value()
            );

            response.getWriter()
                    .write(
                            "Token expired"
                    );

            return;

        } catch (JwtException | IllegalArgumentException e) {

            log.error(
                    "JWT 처리 실패",
                    e
            );

            response.setStatus(
                    HttpStatus.UNAUTHORIZED.value()
            );

            response.getWriter()
                    .write(
                            "Invalid token"
                    );

            return;
        }

        filterChain.doFilter(
                request,
                response
        );
    }
}