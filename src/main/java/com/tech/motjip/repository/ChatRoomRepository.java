package com.tech.motjip.repository;

import com.tech.motjip.domain.ChatRoom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository
        extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findByRoomType(
            String roomType
    );

    List<ChatRoom> findByRoomIdIn(
            List<Long> roomIds
    );

    Optional<ChatRoom> findByInviteCode(
            String inviteCode
    );
}