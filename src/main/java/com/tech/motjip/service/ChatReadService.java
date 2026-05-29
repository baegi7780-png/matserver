package com.tech.motjip.service;

import com.tech.motjip.dto.responseDto.ChatReadEventDto;
import com.tech.motjip.dto.responseDto.ChatRoomUpdateDto;
import com.tech.motjip.repository.ChatMessageRepository;
import com.tech.motjip.repository.ChatRoomMemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatReadService {

    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void updateReadTime(
            Long roomId,
            Long memberId
    ) {

        chatRoomMemberRepository.findByRoomIdAndMemberId(
                roomId,
                memberId
        ).ifPresent(member -> {

            if (Boolean.FALSE.equals(
                    member.getIsLeft()
            )) {

                LocalDateTime previousLastReadAt =
                        member.getLastReadAt();

                LocalDateTime readAt =
                        LocalDateTime.now();

                List<Long> readMessageIds =
                        chatMessageRepository.findReadMessageIds(
                                roomId,
                                memberId,
                                previousLastReadAt,
                                readAt
                        );

                member.setLastReadAt(
                        readAt
                );

                chatRoomMemberRepository.save(
                        member
                );

                Map<Long, Integer> readUnreadCountMap =
                        new HashMap<>();

                if (!readMessageIds.isEmpty()) {

                    List<Object[]> unreadResults =
                            chatMessageRepository
                                    .countUnreadMessagesByMessageIds(
                                            readMessageIds
                                    );

                    for (Object[] row : unreadResults) {

                        Long messageId =
                                (Long) row[0];

                        Long unreadCount =
                                (Long) row[1];

                        readUnreadCountMap.put(
                                messageId,
                                unreadCount.intValue()
                        );
                    }

                    for (Long messageId : readMessageIds) {

                        readUnreadCountMap.putIfAbsent(
                                messageId,
                                0
                        );
                    }
                }

                ChatReadEventDto readEvent =
                        new ChatReadEventDto(
                                roomId,
                                memberId,
                                "READ",
                                readMessageIds,
                                readUnreadCountMap
                        );

                messagingTemplate.convertAndSend(
                        "/sub/chat/room/" + roomId + "/read",
                        readEvent
                );

                ChatRoomUpdateDto roomUpdate =
                        new ChatRoomUpdateDto();

                roomUpdate.setRoomId(
                        roomId
                );

                roomUpdate.setUnreadCount(
                        0
                );

                roomUpdate.setTargetMemberId(
                        memberId
                );

                messagingTemplate.convertAndSend(
                        "/sub/chat/rooms/update/" + memberId,
                        roomUpdate
                );
            }
        });
    }
}