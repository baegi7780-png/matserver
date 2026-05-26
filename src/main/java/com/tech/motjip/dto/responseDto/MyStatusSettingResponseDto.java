package com.tech.motjip.dto.responseDto;

import com.tech.motjip.domain.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyStatusSettingResponseDto {

    private Boolean rejectFriendRequest;

    private Boolean rejectChat;

    private Boolean rejectFriendRecommend;

    private Boolean rejectCommunityInvite;

    public static MyStatusSettingResponseDto from(
            Member member
    ) {

        return MyStatusSettingResponseDto.builder()
                .rejectFriendRequest(member.getRejectFriendRequest())
                .rejectChat(member.getRejectChat())
                .rejectFriendRecommend(member.getRejectFriendRecommend())
                .rejectCommunityInvite(member.getRejectCommunityInvite())
                .build();
    }
}