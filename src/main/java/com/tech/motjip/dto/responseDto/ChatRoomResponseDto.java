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

    // true = 사용자가 직접 입력한 제목
    // false = 참여자 닉네임 기반 동적 제목
    private boolean customRoomName;

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