package com.tech.motjip.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoomResponseDto {

    private Long roomId;

    private String roomName;

    private String roomType;

    private String lastMessage;

    private String lastMessageType;

    private long unreadCount;

    private String time;

    private List<String> participantProfileImages;

    private String inviteUrl;


    // DIRECT 상대 이름
    private String opponentNickname;

    private LocalDateTime lastMessageTime;


    public String getLastMessageType() {
        return lastMessageType;
    }

    public void setLastMessageType(String lastMessageType) {
        this.lastMessageType = lastMessageType;
    }

    public List<String> getParticipantProfileImages() {

        return participantProfileImages;
    }

    public void setParticipantProfileImages(
            List<String> participantProfileImages
    ) {

        this.participantProfileImages =
                participantProfileImages;
    }

}