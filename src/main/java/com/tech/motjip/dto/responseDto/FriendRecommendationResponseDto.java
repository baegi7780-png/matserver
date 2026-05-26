package com.tech.motjip.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FriendRecommendationResponseDto {

    private Long memberId;

    private String nickname;

    private String profileImgUrl;

    private double distanceKm;

    private String status;

    private Long friendRequestId;
}