package com.tech.motjip.controller;

import com.tech.motjip.dto.requestDto.FcmTokenRequestDto;
import com.tech.motjip.dto.requestDto.LogoutRequestDto;
import com.tech.motjip.dto.requestDto.RefreshRequestDto;

import com.tech.motjip.dto.responseDto.TokenResponseDto;

import com.tech.motjip.service.AuthService;

import com.tech.motjip.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponseDto> refresh(
            @RequestBody RefreshRequestDto requestDto
    ) {

        TokenResponseDto response =
                authService.refresh(
                        requestDto.getRefreshToken()
                );

        return ResponseEntity.ok(
                response
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestBody LogoutRequestDto requestDto
    ) {

        authService.logout(
                requestDto.getRefreshToken()
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/me/fcm-token")
    public ResponseEntity<Void> updateFcmToken(

            @RequestHeader("Authorization")
            String authorizationHeader,

            @RequestBody
            FcmTokenRequestDto requestDto
    ) {

        String token =
                authorizationHeader.replace(
                        "Bearer ",
                        ""
                );

        String email =
                jwtTokenProvider.getSubjectFromToken(
                        token
                );

        authService.updateFcmToken(
                email,
                requestDto.getFcmToken()
        );

        return ResponseEntity.ok().build();
    }
}