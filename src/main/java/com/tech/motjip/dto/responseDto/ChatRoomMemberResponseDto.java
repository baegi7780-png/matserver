package com.tech.motjip.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomMemberResponseDto {

    private Long memberId;

    private String nickname;

    private String emailId;

    private String profileImgUrl;
}