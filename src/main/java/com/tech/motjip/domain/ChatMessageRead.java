package com.tech.motjip.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "chat_message_reads",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_chat_message_read",
                        columnNames = {
                                "message_id",
                                "member_id"
                        }
                )
        }
)
public class ChatMessageRead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "message_id",
            nullable = false
    )
    private Long messageId;

    @Column(
            name = "room_id",
            nullable = false
    )
    private Long roomId;

    @Column(
            name = "member_id",
            nullable = false
    )
    private Long memberId;

    @Column(
            name = "read_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime readAt;

    @PrePersist
    protected void onCreate() {

        if (this.readAt == null) {

            this.readAt = LocalDateTime.now();
        }
    }
}