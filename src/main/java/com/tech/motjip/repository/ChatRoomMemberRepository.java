package com.tech.motjip.repository;

import com.tech.motjip.domain.ChatRoomMember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomMemberRepository
        extends JpaRepository<ChatRoomMember, Long> {

    List<ChatRoomMember> findByRoomId(
            Long roomId
    );

    List<ChatRoomMember> findByRoomIdAndIsLeftFalse(
            Long roomId
    );

    List<ChatRoomMember> findByMemberIdAndIsLeftFalse(
            Long memberId
    );

    Optional<ChatRoomMember> findByRoomIdAndMemberId(
            Long roomId,
            Long memberId
    );

    boolean existsByRoomIdAndMemberIdAndIsLeftFalse(
            Long roomId,
            Long memberId
    );

    boolean existsByRoomIdAndMemberIdAndIsLeftTrue(
            Long roomId,
            Long memberId
    );

    long countByRoomIdAndIsLeftFalse(
            Long roomId
    );

    Optional<ChatRoomMember> findTopByRoomIdAndMemberIdNotAndIsLeftFalse(
            Long roomId,
            Long memberId
    );

    List<ChatRoomMember> findByRoomIdInAndIsLeftFalse(
            List<Long> roomIds
    );
}