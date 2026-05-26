package com.tech.motjip.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class LoginResponseDto implements Serializable {

    private Long memberId;
    private String email;
    private String nickname;

    private String accessToken;
    private String refreshToken;
    private String profileImgUrl;

    private boolean isNewUser;

    @Builder
    public LoginResponseDto(Long memberId,
                            String email,
                            String nickname,
                            String accessToken,
                            String refreshToken,
                            String profileImgUrl,
                            boolean isNewUser) {

        this.memberId = memberId;
        this.email = email;
        this.nickname = nickname;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileImgUrl = profileImgUrl;
        this.isNewUser = isNewUser;
    }
}