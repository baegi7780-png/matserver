package com.tech.motjip.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "communities")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "com_id")
    private Long comId;

    @Column(name = "board_type_id", nullable = false)
    private Integer boardTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 🚀 [핵심 수정]
    // 채팅방 ID
    @Column(name = "room_id")
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "region", length = 50)
    private String region;

    @Column(name = "place_name", length = 100)
    private String placeName;

    @Column(name = "place_lat")
    private Double placeLat;

    @Column(name = "place_lng")
    private Double placeLng;

    @Column(name = "meeting_at")
    private LocalDateTime meetingAt;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // 채팅방 링크
    // 선택 입력 가능
    @Column(name = "chat_link", length = 255)
    private String chatLink;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Community(
            Integer boardTypeId,
            Member member,
            Long roomId,
            Tag tag,
            String title,
            String content,
            String region,
            String placeName,
            Double placeLat,
            Double placeLng,
            LocalDateTime meetingAt,
            String imageUrl,
            String chatLink
    ) {

        this.boardTypeId = boardTypeId;
        this.member = member;
        this.roomId = roomId;
        this.tag = tag;
        this.title = title;
        this.content = content;
        this.region = region;
        this.placeName = placeName;
        this.placeLat = placeLat;
        this.placeLng = placeLng;
        this.meetingAt = meetingAt;
        this.imageUrl = imageUrl;
        this.chatLink = chatLink;
        this.isDeleted = false;
    }
}