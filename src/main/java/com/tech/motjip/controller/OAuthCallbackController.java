package com.tech.motjip.controller;

import com.tech.motjip.dto.responseDto.GoogleTokenResponse;
import com.tech.motjip.dto.responseDto.GoogleUserInfoResponse;
import com.tech.motjip.dto.requestDto.KakaoSdkLoginRequestDto;
import com.tech.motjip.dto.responseDto.KakaoTokenResponse;
import com.tech.motjip.dto.responseDto.KakaoUserInfoResponse;
import com.tech.motjip.dto.requestDto.LoginRequestDto;
import com.tech.motjip.dto.responseDto.LoginResponseDto;
import com.tech.motjip.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OAuthCallbackController {

    private final MemberService memberService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${kakao.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    @PostMapping("/api/v1/auth/kakao")
    public ResponseEntity<LoginResponseDto> kakaoSdkLogin(
            @RequestBody KakaoSdkLoginRequestDto requestDto
    ) {

        log.info("카카오 SDK 로그인 요청 수신");

        try {
            if (requestDto.getAccessToken() == null ||
                    requestDto.getAccessToken().trim().isEmpty()) {

                throw new IllegalArgumentException("카카오 accessToken이 없습니다.");
            }

            String kakaoAccessToken = requestDto.getAccessToken();

            String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(kakaoAccessToken);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<KakaoUserInfoResponse> userInfoResponse =
                    restTemplate.exchange(
                            userInfoUrl,
                            HttpMethod.GET,
                            entity,
                            KakaoUserInfoResponse.class
                    );

            if (userInfoResponse.getBody() == null ||
                    userInfoResponse.getBody().getKakao_account() == null ||
                    userInfoResponse.getBody().getKakao_account().getEmail() == null) {

                throw new RuntimeException("카카오 사용자 이메일을 가져올 수 없습니다.");
            }

            String email =
                    userInfoResponse.getBody()
                            .getKakao_account()
                            .getEmail();

            log.info("카카오 SDK 로그인 이메일: {}", email);

            LoginRequestDto loginRequestDto = new LoginRequestDto();
            loginRequestDto.setEmail(email);
            loginRequestDto.setProviderId(1);

            LoginResponseDto loginResponse =
                    memberService.loginOrSignup(loginRequestDto);

            return ResponseEntity.ok(loginResponse);

        } catch (Exception e) {
            log.error("카카오 SDK 로그인 처리 중 예외 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }

    @GetMapping("/login/oauth2/code/kakao")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {

        log.info("Kakao Callback received with code: {}", code);

        try {
            String tokenUrl = "https://kauth.kakao.com/oauth/token";

            HttpHeaders tokenHeaders = new HttpHeaders();
            tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> tokenParams = new LinkedMultiValueMap<>();
            tokenParams.add("grant_type", "authorization_code");
            tokenParams.add("client_id", KAKAO_CLIENT_ID);
            tokenParams.add("client_secret", KAKAO_CLIENT_SECRET);
            tokenParams.add("redirect_uri", KAKAO_REDIRECT_URI);
            tokenParams.add("code", code);

            HttpEntity<MultiValueMap<String, String>> tokenRequest =
                    new HttpEntity<>(tokenParams, tokenHeaders);

            ResponseEntity<KakaoTokenResponse> tokenResponse =
                    restTemplate.postForEntity(
                            tokenUrl,
                            tokenRequest,
                            KakaoTokenResponse.class
                    );

            if (tokenResponse.getBody() == null ||
                    tokenResponse.getBody().getAccess_token() == null) {

                throw new RuntimeException("카카오 access token 발급 실패");
            }

            String accessToken = tokenResponse.getBody().getAccess_token();

            log.info("Kakao Access Token retrieved successfully.");

            String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

            HttpHeaders userInfoHeaders = new HttpHeaders();
            userInfoHeaders.setBearerAuth(accessToken);
            userInfoHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> userInfoRequest =
                    new HttpEntity<>(null, userInfoHeaders);

            ResponseEntity<KakaoUserInfoResponse> userInfoResponse =
                    restTemplate.postForEntity(
                            userInfoUrl,
                            userInfoRequest,
                            KakaoUserInfoResponse.class
                    );

            if (userInfoResponse.getBody() == null ||
                    userInfoResponse.getBody().getKakao_account() == null ||
                    userInfoResponse.getBody().getKakao_account().getEmail() == null) {

                throw new RuntimeException("카카오 사용자 이메일 조회 실패");
            }

            String email =
                    userInfoResponse.getBody()
                            .getKakao_account()
                            .getEmail();

            log.info("Kakao User Email retrieved: {}", email);

            return processLoginOrSignupWithRedirect(email, 1);

        } catch (Exception e) {
            log.error("카카오 로그인 콜백 처리 중 예외 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process kakao callback");
        }
    }

    @GetMapping("/login/oauth2/code/google")
    public ResponseEntity<?> googleCallback(@RequestParam("code") String code) {

        log.info("Google Callback received with code: {}", code);

        try {
            String tokenUrl = "https://oauth2.googleapis.com/token";

            MultiValueMap<String, String> tokenParams =
                    new LinkedMultiValueMap<>();

            tokenParams.add("code", code);
            tokenParams.add("client_id", GOOGLE_CLIENT_ID);
            tokenParams.add("client_secret", GOOGLE_CLIENT_SECRET);
            tokenParams.add("redirect_uri", GOOGLE_REDIRECT_URI);
            tokenParams.add("grant_type", "authorization_code");

            ResponseEntity<GoogleTokenResponse> tokenResponse =
                    restTemplate.postForEntity(
                            tokenUrl,
                            tokenParams,
                            GoogleTokenResponse.class
                    );

            if (tokenResponse.getBody() == null ||
                    tokenResponse.getBody().getAccess_token() == null) {

                throw new RuntimeException("구글 access token 발급 실패");
            }

            String accessToken =
                    tokenResponse.getBody().getAccess_token();

            log.info("Google Access Token retrieved.");

            String userInfoUrl =
                    "https://www.googleapis.com/oauth2/v3/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> entity =
                    new HttpEntity<>(headers);

            ResponseEntity<GoogleUserInfoResponse> userInfoResponse =
                    restTemplate.exchange(
                            userInfoUrl,
                            HttpMethod.GET,
                            entity,
                            GoogleUserInfoResponse.class
                    );

            if (userInfoResponse.getBody() == null ||
                    userInfoResponse.getBody().getEmail() == null) {

                throw new RuntimeException("구글 사용자 이메일 조회 실패");
            }

            String email =
                    userInfoResponse.getBody().getEmail();

            log.info("Google User Email retrieved: {}", email);

            return processLoginOrSignupWithRedirect(email, 2);

        } catch (Exception e) {
            log.error("구글 로그인 콜백 처리 중 예외 발생", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process google callback");
        }
    }

    private ResponseEntity<?> processLoginOrSignupWithRedirect(
            String email,
            int providerId
    ) {

        LoginRequestDto requestDto = new LoginRequestDto();
        requestDto.setEmail(email);
        requestDto.setProviderId(providerId);

        LoginResponseDto loginResponse =
                memberService.loginOrSignup(requestDto);

        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();
        Long memberId = loginResponse.getMemberId();
        boolean isNewUser = loginResponse.isNewUser();

        String redirectUrl =
                "motjip://main" +
                        "?access_token=" + accessToken +
                        "&refresh_token=" + refreshToken +
                        "&member_id=" + memberId +
                        "&is_new_user=" + isNewUser;

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }
}