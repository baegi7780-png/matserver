package com.tech.motjip.dto.requestDto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateMyStatusSettingRequestDto {

    private Boolean rejectFriendRequest;

    private Boolean rejectChat;

    private Boolean rejectFriendRecommend;

    private Boolean rejectCommunityInvite;
}