package com.tech.motjip.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomUpdateDto {

    private Long roomId;

    private String lastMessage;

    private String lastMessageType;

    private String time;

    private int unreadCount;

    // 어떤 사용자의 unread 변경인지
    private Long targetMemberId;
}