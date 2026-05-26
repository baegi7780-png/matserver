package com.tech.motjip.service;

import com.tech.motjip.domain.Member;
import com.tech.motjip.domain.Provider;
import com.tech.motjip.dto.requestDto.LoginRequestDto;
import com.tech.motjip.dto.requestDto.NicknameUpdateRequestDto;
import com.tech.motjip.dto.requestDto.StatusUpdateRequestDto;
import com.tech.motjip.dto.requestDto.UpdateLocationRequestDto;
import com.tech.motjip.dto.requestDto.UpdateMyNicknameRequestDto;
import com.tech.motjip.dto.responseDto.LoginResponseDto;
import com.tech.motjip.repository.MemberRepository;
import com.tech.motjip.repository.ProviderRepository;
import com.tech.motjip.util.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProviderRepository providerRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Transactional
    public LoginResponseDto loginOrSignup(LoginRequestDto requestDto) {

        if (requestDto.getEmail() == null ||
                requestDto.getEmail().trim().isEmpty()) {

            throw new IllegalArgumentException("로그인 요청에 이메일 정보가 없습니다.");
        }

        if (requestDto.getProviderId() == null) {

            throw new IllegalArgumentException("로그인 요청에 providerId 정보가 없습니다.");
        }

        Provider provider =
                providerRepository.findById(requestDto.getProviderId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 providerId 입니다: "
                                                + requestDto.getProviderId()
                                )
                        );

        Optional<Member> existingMember =
                memberRepository.findByProviderIdAndEmailId(
                        requestDto.getProviderId(),
                        requestDto.getEmail()
                );

        if (existingMember.isPresent()) {

            Member member = existingMember.get();

            member.updateStatusCode(1);

            boolean isNewUser =
                    member.getNickname() == null ||
                            member.getNickname().isEmpty();

            String accessToken =
                    jwtTokenProvider.createAccessToken(
                            member.getEmailId(),
                            member.getMemberId()
                    );

            String refreshToken =
                    jwtTokenProvider.createRefreshToken(
                            member.getEmailId(),
                            member.getMemberId()
                    );

            authService.saveRefreshToken(
                    member.getEmailId(),
                    refreshToken
            );

            return LoginResponseDto.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmailId())
                    .nickname(member.getNickname())
                    .profileImgUrl(member.getProfileImgUrl())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .isNewUser(isNewUser)
                    .build();
        }

        Member newMember =
                Member.createNewMember(
                        requestDto.getEmail(),
                        provider
                );

        newMember.updateStatusCode(1);

        memberRepository.save(newMember);

        String accessToken =
                jwtTokenProvider.createAccessToken(
                        newMember.getEmailId(),
                        newMember.getMemberId()
                );

        String refreshToken =
                jwtTokenProvider.createRefreshToken(
                        newMember.getEmailId(),
                        newMember.getMemberId()
                );

        authService.saveRefreshToken(
                newMember.getEmailId(),
                refreshToken
        );

        return LoginResponseDto.builder()
                .memberId(newMember.getMemberId())
                .email(newMember.getEmailId())
                .nickname(null)
                .profileImgUrl(newMember.getProfileImgUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(true)
                .build();
    }

    public LoginResponseDto getMyInfo(String email) {

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new RuntimeException("사용자를 찾을 수 없습니다.")
                        );

        return LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmailId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl())
                .accessToken(null)
                .refreshToken(null)
                .isNewUser(false)
                .build();
    }

    @Transactional
    public LoginResponseDto updateNickname(
            String token,
            NicknameUpdateRequestDto requestDto
    ) {

        if (!jwtTokenProvider.validateToken(token)) {

            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }

        Member member =
                memberRepository.findById(requestDto.getMemberId())
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 회원입니다.")
                        );

        String tokenEmail =
                jwtTokenProvider.getSubjectFromToken(token);

        if (!member.getEmailId().equals(tokenEmail)) {

            throw new IllegalArgumentException("토큰 정보가 일치하지 않습니다.");
        }

        member.updateNickname(requestDto.getNickname());
        member.updateStatusCode(1);

        String accessToken =
                jwtTokenProvider.createAccessToken(
                        member.getEmailId(),
                        member.getMemberId()
                );

        String refreshToken =
                jwtTokenProvider.createRefreshToken(
                        member.getEmailId(),
                        member.getMemberId()
                );

        authService.saveRefreshToken(
                member.getEmailId(),
                refreshToken
        );

        org.springframework.security.core.Authentication authentication =
                jwtTokenProvider.getAuthentication(accessToken);

        org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        return LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmailId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(false)
                .build();
    }

    @Transactional
    public LoginResponseDto updateMyNickname(
            String token,
            UpdateMyNicknameRequestDto requestDto
    ) {

        if (!jwtTokenProvider.validateToken(token)) {

            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }

        String email =
                jwtTokenProvider.getSubjectFromToken(token);

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 회원입니다.")
                        );

        String nickname =
                requestDto.getNickname();

        if (nickname == null || nickname.trim().isEmpty()) {

            throw new IllegalArgumentException("닉네임을 입력해 주세요.");
        }

        nickname = nickname.trim();

        if (nickname.length() < 2 || nickname.length() > 10) {

            throw new IllegalArgumentException("닉네임은 2~10자 이내로 입력해 주세요.");
        }

        member.updateNickname(nickname);

        return LoginResponseDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmailId())
                .nickname(member.getNickname())
                .profileImgUrl(member.getProfileImgUrl())
                .accessToken(null)
                .refreshToken(null)
                .isNewUser(false)
                .build();
    }

    @Transactional
    public LoginResponseDto updateProfileImage(
            String token,
            MultipartFile image
    ) {

        if (!jwtTokenProvider.validateToken(token)) {

            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }

        String email =
                jwtTokenProvider.getSubjectFromToken(token);

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException("존재하지 않는 회원입니다.")
                        );

        if (image == null || image.isEmpty()) {

            throw new IllegalArgumentException("이미지 파일이 없습니다.");
        }

        try {

            String uploadDir =
                    System.getProperty("user.dir")
                            + "/uploads/profile/";

            File directory =
                    new File(uploadDir);

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String originalFilename =
                    image.getOriginalFilename();

            String extension = ".jpg";

            if (originalFilename != null &&
                    originalFilename.contains(".")) {

                extension =
                        originalFilename.substring(
                                originalFilename.lastIndexOf(".")
                        );
            }

            String savedFileName =
                    UUID.randomUUID() + extension;

            File destination =
                    new File(uploadDir + savedFileName);

            image.transferTo(destination);

            String profileImageUrl =
                    "/uploads/profile/" + savedFileName;

            member.updateProfileImgUrl(profileImageUrl);

            return LoginResponseDto.builder()
                    .memberId(member.getMemberId())
                    .email(member.getEmailId())
                    .nickname(member.getNickname())
                    .profileImgUrl(member.getProfileImgUrl())
                    .accessToken(null)
                    .refreshToken(null)
                    .isNewUser(false)
                    .build();

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException("프로필 이미지 저장 실패");
        }
    }

    @Transactional
    public void updateMyStatus(
            String token,
            StatusUpdateRequestDto requestDto
    ) {

        if (!jwtTokenProvider.validateToken(token)) {

            throw new IllegalArgumentException(
                    "유효하지 않거나 만료된 토큰입니다."
            );
        }

        String email =
                jwtTokenProvider.getSubjectFromToken(token);

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 회원입니다."
                                )
                        );

        Integer statusCode =
                requestDto.getStatusCode();

        if (statusCode == null) {

            throw new IllegalArgumentException(
                    "statusCode 값이 없습니다."
            );
        }

        member.updateStatusCode(statusCode);
    }

    @Transactional
    public void updateMyLocation(
            String token,
            UpdateLocationRequestDto requestDto
    ) {

        if (!jwtTokenProvider.validateToken(token)) {

            throw new IllegalArgumentException(
                    "유효하지 않거나 만료된 토큰입니다."
            );
        }

        String email =
                jwtTokenProvider.getSubjectFromToken(token);

        Member member =
                memberRepository.findByEmailId(email)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "존재하지 않는 회원입니다."
                                )
                        );

        Double latitude =
                requestDto.getLatitude();

        Double longitude =
                requestDto.getLongitude();

        if (latitude == null || longitude == null) {

            throw new IllegalArgumentException(
                    "위치 정보가 없습니다."
            );
        }

        member.updateLocation(
                latitude,
                longitude
        );
    }
}