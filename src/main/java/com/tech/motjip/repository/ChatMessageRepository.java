package com.tech.motjip.repository;

import com.tech.motjip.domain.ChatMessage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findByRoomIdOrderBySentAtAsc(
            Long roomId
    );

    List<ChatMessage> findByRoomIdOrderBySentAtDesc(
            Long roomId,
            Pageable pageable
    );

    ChatMessage findTopByRoomIdOrderBySentAtDesc(
            Long roomId
    );

    @Query(value = """
            SELECT cm.*
            FROM chat_messages cm
            INNER JOIN (
                SELECT room_id,
                       MAX(id) AS max_id
                FROM chat_messages
                WHERE room_id IN (:roomIds)
                GROUP BY room_id
            ) latest
            ON cm.id = latest.max_id
            """,
            nativeQuery = true)
    List<ChatMessage> findLatestMessagesByRoomIds(
            @Param("roomIds") List<Long> roomIds
    );

    @Query("""
            SELECT m.roomId, COUNT(m)
            FROM ChatMessage m
            JOIN ChatRoomMember crm
                ON crm.roomId = m.roomId
            WHERE m.roomId IN :roomIds
            AND crm.memberId = :memberId
            AND crm.isLeft = false
            AND m.senderId <> :memberId
            AND m.messageType <> 'SYSTEM'
            AND (
                crm.lastReadAt IS NULL
                OR m.sentAt > crm.lastReadAt
            )
            GROUP BY m.roomId
            """)
    List<Object[]> countUnreadMessagesByRoomIds(
            @Param("roomIds") List<Long> roomIds,
            @Param("memberId") Long memberId
    );

    @Query("""
            SELECT m.id, COUNT(crm)
            FROM ChatMessage m
            JOIN ChatRoomMember crm
                ON crm.roomId = m.roomId
            WHERE m.id IN :messageIds
            AND crm.isLeft = false
            AND crm.memberId <> m.senderId
            AND m.messageType <> 'SYSTEM'
            AND (
                crm.lastReadAt IS NULL
                OR m.sentAt > crm.lastReadAt
            )
            GROUP BY m.id
            """)
    List<Object[]> countUnreadMessagesByMessageIds(
            @Param("messageIds") List<Long> messageIds
    );

    long countByRoomIdAndSenderIdNotAndSentAtAfter(
            Long roomId,
            Long senderId,
            LocalDateTime lastReadAt
    );

    long countByRoomIdAndSenderIdNotAndMessageTypeNotAndSentAtAfter(
            Long roomId,
            Long senderId,
            String messageType,
            LocalDateTime lastReadAt
    );

    void deleteByRoomId(
            Long roomId
    );

    @Query("""
            SELECT m.id
            FROM ChatMessage m
            WHERE m.roomId = :roomId
            AND m.senderId <> :readerId
            AND m.messageType <> 'SYSTEM'
            AND (
                :lastReadAt IS NULL
                OR m.sentAt > :lastReadAt
            )
            AND m.sentAt <= :readAt
            ORDER BY m.sentAt ASC
            """)
    List<Long> findReadMessageIds(
            @Param("roomId") Long roomId,
            @Param("readerId") Long readerId,
            @Param("lastReadAt") LocalDateTime lastReadAt,
            @Param("readAt") LocalDateTime readAt
    );
}