package com.tech.motjip.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "chat_room_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "last_read_at")
    private LocalDateTime lastReadAt;

    @Column(name = "is_left", nullable = false)
    private Boolean isLeft = false;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    public void leave() {
        this.isLeft = true;
        this.leftAt = LocalDateTime.now();
    }

    public void rejoin() {
        this.isLeft = false;
        this.leftAt = null;
        this.lastReadAt = LocalDateTime.now();
    }
    public boolean isActiveMember() {
        return Boolean.FALSE.equals(this.isLeft);
    }
}