package com.tech.motjip.config;

import com.tech.motjip.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                )

                .httpBasic(httpBasic ->
                        httpBasic.disable()
                )

                .csrf(csrf ->
                        csrf.disable()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(

                                "/",
                                "/error",

                                "/uploads/**",

                                "/fcm/test",

                                "/api/v1/auth/login",
                                "/api/v1/auth/kakao",
                                "/api/v1/auth/refresh",

                                "/oauth/callback/kakao",
                                "/oauth/callback/google",

                                "/login/oauth2/code/kakao",
                                "/login/oauth2/code/google"

                        ).permitAll()

                        .requestMatchers(

                                "/api/v1/user/**",

                                "/api/v1/auth/nickname",

                                "/api/v1/auth/me/nickname",

                                "/api/v1/auth/me/profile-image",

                                "/api/v1/auth/logout",

                                "/api/v1/community/posts",
                                "/api/v1/community/posts/**",

                                "/api/v1/favorites/**",

                                "/api/v1/friends/**",

                                "/api/v1/community-invites/**",

                                "/api/v1/member-status/**",

                                "/notifications"

                        ).authenticated()

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        new JwtAuthenticationFilter(
                                jwtTokenProvider
                        ),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOriginPatterns(
                Arrays.asList("*")
        );

        configuration.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                Arrays.asList("*")
        );

        configuration.setExposedHeaders(
                Arrays.asList(
                        "Authorization"
                )
        );

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}