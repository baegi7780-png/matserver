package com.tech.motjip.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NicknameUpdateRequestDto {
    private Long memberId;
    private String nickname;
}