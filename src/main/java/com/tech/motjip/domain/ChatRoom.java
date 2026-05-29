package com.tech.motjip.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(name = "room_name")
    private String roomName;

    // true  = 사용자가 직접 입력한 채팅방 이름 영구 유지
    // false = 참여자 닉네임 기반 동적 제목 사용
    @Column(name = "custom_room_name", nullable = false)
    private boolean customRoomName = false;

    // DIRECT / GROUP
    @Column(name = "room_type")
    private String roomType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(
            name = "invite_code",
            unique = true,
            length = 100
    )
    private String inviteCode;

    @Transient
    private String inviteUrl;

    @Transient
    private LocalDateTime lastMessageTime;

    @PrePersist
    protected void onCreate() {

        this.createdAt =
                LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {

        return createdAt;
    }

    public LocalDateTime getLastMessageTime() {

        return lastMessageTime;
    }

    public void setLastMessageTime(
            LocalDateTime lastMessageTime
    ) {

        this.lastMessageTime =
                lastMessageTime;
    }

    public String getInviteCode() {

        return inviteCode;
    }

    public void setInviteCode(
            String inviteCode
    ) {

        this.inviteCode =
                inviteCode;
    }

    public String getInviteUrl() {

        return inviteUrl;
    }

    public void setInviteUrl(
            String inviteUrl
    ) {

        this.inviteUrl =
                inviteUrl;
    }
}