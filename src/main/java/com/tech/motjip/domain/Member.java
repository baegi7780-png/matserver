package com.tech.motjip.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_provider_email", columnNames = {"provider_id", "email_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "email_id", nullable = false)
    private String emailId;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "profile_img_url", length = 500)
    private String profileImgUrl;

    @Column(name = "fcm_token", length = 500)
    private String fcmToken;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "location_enabled")
    private Boolean locationEnabled;

    @Column(name = "reject_friend_request")
    private Boolean rejectFriendRequest;

    @Column(name = "reject_chat")
    private Boolean rejectChat;

    @Column(name = "reject_friend_recommend")
    private Boolean rejectFriendRecommend;

    @Column(name = "reject_community_invite")
    private Boolean rejectCommunityInvite;

    @Column(name = "location_updated_at")
    private LocalDateTime locationUpdatedAt;

    @Column(name = "status_code")
    private Integer statusCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", foreignKey = @ForeignKey(name = "fk_member_provider"))
    private Provider provider;

    @Column(name = "provider_id", insertable = false, updatable = false)
    private Integer providerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Member(
            String emailId,
            String nickname,
            String profileImgUrl,
            String fcmToken,
            Double latitude,
            Double longitude,
            Boolean locationEnabled,
            Boolean rejectFriendRequest,
            Boolean rejectChat,
            Boolean rejectFriendRecommend,
            Boolean rejectCommunityInvite,
            LocalDateTime locationUpdatedAt,
            Integer statusCode,
            Provider provider
    ) {

        this.emailId = emailId;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.fcmToken = fcmToken;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationEnabled = locationEnabled != null ? locationEnabled : true;
        this.rejectFriendRequest = rejectFriendRequest != null ? rejectFriendRequest : false;
        this.rejectChat = rejectChat != null ? rejectChat : false;
        this.rejectFriendRecommend = rejectFriendRecommend != null ? rejectFriendRecommend : false;
        this.rejectCommunityInvite = rejectCommunityInvite != null ? rejectCommunityInvite : false;
        this.locationUpdatedAt = locationUpdatedAt;
        this.statusCode = statusCode;
        this.provider = provider;
    }

    public static Member createNewMember(
            String emailId,
            Provider provider
    ) {

        if (emailId == null || emailId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "이메일은 필수 입력값입니다."
            );
        }

        return Member.builder()
                .emailId(emailId)
                .provider(provider)
                .statusCode(1)
                .locationEnabled(true)
                .rejectFriendRequest(false)
                .rejectChat(false)
                .rejectFriendRecommend(false)
                .rejectCommunityInvite(false)
                .build();
    }

    public static Member createNewMember(
            String emailId,
            Integer providerId
    ) {

        if (emailId == null || emailId.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "이메일은 필수 입력값입니다."
            );
        }

        return Member.builder()
                .emailId(emailId)
                .statusCode(1)
                .locationEnabled(true)
                .rejectFriendRequest(false)
                .rejectChat(false)
                .rejectFriendRecommend(false)
                .rejectCommunityInvite(false)
                .build();
    }

    public void updateNickname(
            String nickname
    ) {

        this.nickname = nickname;
    }

    public void updateProfileImgUrl(
            String profileImgUrl
    ) {

        this.profileImgUrl = profileImgUrl;
    }

    public void updateFcmToken(
            String fcmToken
    ) {

        this.fcmToken = fcmToken;
    }

    public void updateLocation(
            Double latitude,
            Double longitude
    ) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.locationEnabled = true;
        this.locationUpdatedAt = LocalDateTime.now();
    }

    public void updateLocationEnabled(
            Boolean locationEnabled
    ) {

        this.locationEnabled = locationEnabled;
    }

    public void updateStatusCode(
            Integer statusCode
    ) {

        this.statusCode = statusCode;
    }

    public void updateRejectFriendRequest(
            Boolean rejectFriendRequest
    ) {

        this.rejectFriendRequest = rejectFriendRequest;
    }

    public void updateRejectChat(
            Boolean rejectChat
    ) {

        this.rejectChat = rejectChat;
    }

    public void updateRejectFriendRecommend(
            Boolean rejectFriendRecommend
    ) {

        this.rejectFriendRecommend = rejectFriendRecommend;
    }

    public void updateRejectCommunityInvite(
            Boolean rejectCommunityInvite
    ) {

        this.rejectCommunityInvite = rejectCommunityInvite;
    }

    public void updateMyStatusSettings(
            Boolean rejectFriendRequest,
            Boolean rejectChat,
            Boolean rejectFriendRecommend,
            Boolean rejectCommunityInvite
    ) {

        this.rejectFriendRequest = rejectFriendRequest;
        this.rejectChat = rejectChat;
        this.rejectFriendRecommend = rejectFriendRecommend;
        this.rejectCommunityInvite = rejectCommunityInvite;
    }
}