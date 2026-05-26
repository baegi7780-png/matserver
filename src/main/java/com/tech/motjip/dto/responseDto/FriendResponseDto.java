package com.tech.motjip.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FriendResponseDto {

    private Long memberId;

    private String nickname;

    private String profileImgUrl;
}