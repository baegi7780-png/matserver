package com.tech.motjip.dto.requestDto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NicknameRequest {
    private Long memberId;
    private String nickname;

    @Builder
    public NicknameRequest(Long memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }
}