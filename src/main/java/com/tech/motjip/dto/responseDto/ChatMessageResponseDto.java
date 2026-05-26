package com.tech.motjip.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponseDto {

    private Long id;

    private Long roomId;

    private Long senderId;

    private String senderNickname;

    private String senderProfileImage;

    private String messageContent;

    private String messageType;

    private String fileUrl;

    private String sentAt;

    private int unreadCount;
}