package com.tech.motjip.repository;

import com.tech.motjip.domain.ChatMessageRead;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageReadRepository
        extends JpaRepository<ChatMessageRead, Long> {

    boolean existsByMessageIdAndMemberId(
            Long messageId,
            Long memberId
    );

    long countByMessageId(
            Long messageId
    );

    List<ChatMessageRead> findByMessageIdIn(
            List<Long> messageIds
    );

    List<ChatMessageRead> findByRoomIdAndMemberId(
            Long roomId,
            Long memberId
    );

    long countByMessageIdAndMemberIdNot(
            Long messageId,
            Long memberId
    );
}