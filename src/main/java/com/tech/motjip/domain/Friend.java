package com.tech.motjip.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "friends",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_friend_pair",
                        columnNames = {
                                "member_id",
                                "friend_member_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friend_id")
    private Long friendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_member_id", nullable = false)
    private Member friendMember;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt =
            LocalDateTime.now();
}