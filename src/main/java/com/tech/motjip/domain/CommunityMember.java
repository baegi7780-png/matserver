package com.tech.motjip.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "community_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_community_member",
                        columnNames = {"com_id", "member_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "community_member_id")
    private Long communityMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "com_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "role", length = 20)
    private String role;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "hidden", nullable = false)
    private boolean hidden = false;

    @CreationTimestamp
    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @Builder
    public CommunityMember(
            Community community,
            Member member,
            String role,
            String status
    ) {
        this.community = community;
        this.member = member;
        this.role = role;
        this.status = status;
        this.hidden = false;
    }
}