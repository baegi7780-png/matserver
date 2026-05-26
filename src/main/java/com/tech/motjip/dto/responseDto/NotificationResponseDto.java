package com.tech.motjip.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDto {

    private Long notificationId;

    private String senderNickname;

    private String type;

    private Long targetId;

    private String message;

    private String status;

    @JsonProperty("isRead")
    private boolean isRead;

    private LocalDateTime createdAt;
}