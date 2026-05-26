package com.tech.motjip.dto.responseDto;

import com.tech.motjip.domain.CommunityMember;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommunityMemberResponseDto {

    private Long memberId;

    private String nickname;

    private String profileImageUrl;

    private String role;

    private boolean friend;

    // 🔥 추가
    private String friendStatus;

    public static CommunityMemberResponseDto from(
            CommunityMember communityMember,
            boolean friend
    ) {

        return CommunityMemberResponseDto.builder()
                .memberId(
                        communityMember.getMember().getMemberId()
                )
                .nickname(
                        communityMember.getMember().getNickname()
                )
                .profileImageUrl(
                        communityMember.getMember().getProfileImgUrl()
                )
                .role(
                        communityMember.getRole()
                )
                .friend(
                        friend
                )
                .build();
    }
}