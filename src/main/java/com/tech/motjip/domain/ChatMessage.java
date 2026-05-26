package com.tech.motjip.domain;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "chat_messages")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "room_id",
            nullable = false
    )
    private Long roomId;

    @Column(
            name = "sender_id",
            nullable = false
    )
    private Long senderId;

    @Column(
            name = "message_content",
            columnDefinition = "TEXT",
            nullable = false
    )
    private String messageContent;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            timezone = "Asia/Seoul"
    )
    @Column(
            name = "sent_at",
            updatable = false
    )
    private LocalDateTime sentAt;

    @Column(name = "sender_nickname")
    private String senderNickname;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "file_url")
    private String fileUrl;

    @Transient
    private int unreadCount;

    @PrePersist
    protected void onCreate() {

        this.sentAt =
                LocalDateTime.now();

        if (this.messageType == null
                || this.messageType.trim().isEmpty()) {

            this.messageType =
                    "TEXT";
        }

        if (this.messageContent == null) {

            this.messageContent =
                    "";
        }
    }
}