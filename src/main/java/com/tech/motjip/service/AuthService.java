package com.tech.motjip.service;

import com.tech.motjip.domain.Member;
import com.tech.motjip.domain.RefreshToken;

import com.tech.motjip.dto.responseDto.TokenResponseDto;

import com.tech.motjip.repository.MemberRepository;
import com.tech.motjip.repository.RefreshTokenRepository;

import com.tech.motjip.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberRepository memberRepository;

    @Transactional
    public void saveRefreshToken(
            String email,
            String refreshToken
    ) {

        LocalDateTime expiry =
                LocalDateTime.now()
                        .plusDays(7);

        refreshTokenRepository.findByEmail(email)
                .ifPresentOrElse(
                        token -> token.updateToken(
                                refreshToken,
                                expiry
                        ),
                        () -> refreshTokenRepository.save(
                                RefreshToken.builder()
                                        .email(email)
                                        .refreshToken(refreshToken)
                                        .expiryDate(expiry)
                                        .build()
                        )
                );
    }

    @Transactional
    public TokenResponseDto refresh(
            String refreshToken
    ) {

        if (!jwtTokenProvider.validateToken(refreshToken)) {

            throw new RuntimeException(
                    "유효하지 않은 refresh token"
            );
        }

        if (!jwtTokenProvider.isRefreshToken(refreshToken)) {

            throw new RuntimeException(
                    "refresh token이 아닙니다"
            );
        }

        RefreshToken savedToken =
                refreshTokenRepository.findByRefreshToken(
                                refreshToken
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "DB에 없는 refresh token"
                                )
                        );

        if (savedToken.getExpiryDate()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException(
                    "refresh token 만료"
            );
        }

        String email =
                savedToken.getEmail();

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자 없음"
                                )
                        );

        String newAccessToken =
                jwtTokenProvider.createAccessToken(
                        member.getEmailId(),
                        member.getMemberId()
                );

        String newRefreshToken =
                jwtTokenProvider.createRefreshToken(
                        member.getEmailId(),
                        member.getMemberId()
                );

        savedToken.updateToken(
                newRefreshToken,
                LocalDateTime.now().plusDays(7)
        );

        return TokenResponseDto.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void logout(
            String refreshToken
    ) {

        if (refreshToken == null
                || refreshToken.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "refreshToken이 없습니다."
            );
        }

        RefreshToken savedToken =
                refreshTokenRepository.findByRefreshToken(
                                refreshToken
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "DB에 없는 refreshToken입니다."
                                )
                        );

        String email =
                savedToken.getEmail();

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        member.updateStatusCode(
                0
        );

        refreshTokenRepository.delete(
                savedToken
        );
    }

    @Transactional
    public void updateFcmToken(
            String emailId,
            String fcmToken
    ) {

        if (fcmToken == null
                || fcmToken.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "FCM 토큰이 없습니다."
            );
        }

        Member member =
                memberRepository.findByEmailId(emailId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "사용자를 찾을 수 없습니다."
                                )
                        );

        member.updateFcmToken(
                fcmToken
        );
    }
}