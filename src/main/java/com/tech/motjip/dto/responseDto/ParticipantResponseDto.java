package com.tech.motjip.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantResponseDto {

    private Long memberId;

    private String nickname;

    private String profileImgUrl;
}