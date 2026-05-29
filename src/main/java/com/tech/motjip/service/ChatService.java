package com.tech.motjip.service;

import com.tech.motjip.domain.ChatMessage;
import com.tech.motjip.domain.ChatRoom;
import com.tech.motjip.domain.ChatRoomMember;
import com.tech.motjip.domain.Member;
import com.tech.motjip.dto.responseDto.ChatRoomUpdateDto;
import com.tech.motjip.repository.ChatMessageRepository;
import com.tech.motjip.repository.ChatRoomMemberRepository;
import com.tech.motjip.repository.ChatRoomRepository;
import com.tech.motjip.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;
    private final FcmService fcmService;

    private final Map<Long, Long> activeChatRoomMap =
            new ConcurrentHashMap<>();

    public void enterChatRoom(
            Long memberId,
            Long roomId
    ) {

        activeChatRoomMap.put(
                memberId,
                roomId
        );

        System.out.println(
                "ENTER_CHAT_ROOM memberId="
                        + memberId
                        + ", roomId="
                        + roomId
                        + ", activeMap="
                        + activeChatRoomMap
        );
    }

    public void exitChatRoom(
            Long memberId,
            Long roomId
    ) {

        Long activeRoomId =
                activeChatRoomMap.get(
                        memberId
                );

        System.out.println(
                "EXIT_CHAT_ROOM_REQUEST memberId="
                        + memberId
                        + ", roomId="
                        + roomId
                        + ", activeRoomId="
                        + activeRoomId
                        + ", activeMapBefore="
                        + activeChatRoomMap
        );

        if (activeRoomId != null
                && activeRoomId.equals(roomId)) {

            activeChatRoomMap.remove(
                    memberId
            );

            System.out.println(
                    "EXIT_CHAT_ROOM_REMOVED memberId="
                            + memberId
                            + ", roomId="
                            + roomId
                            + ", activeMapAfter="
                            + activeChatRoomMap
            );

        } else {

            System.out.println(
                    "EXIT_CHAT_ROOM_SKIPPED memberId="
                            + memberId
                            + ", requestRoomId="
                            + roomId
                            + ", activeRoomId="
                            + activeRoomId
            );
        }
    }

    public boolean isViewingRoom(
            Long memberId,
            Long roomId
    ) {

        Long activeRoomId =
                activeChatRoomMap.get(
                        memberId
                );

        if (activeRoomId == null) {

            return false;
        }

        return activeRoomId.equals(
                roomId
        );
    }

    @Transactional
    public void joinRoomByInviteCode(
            String inviteCode,
            Long memberId
    ) {

        ChatRoom room =
                chatRoomRepository.findByInviteCode(
                                inviteCode
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "유효하지 않은 초대 링크입니다."
                                )
                        );

        Member member =
                memberRepository.findById(
                                memberId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자가 존재하지 않습니다."
                                )
                        );

        Optional<ChatRoomMember> optionalMember =
                chatRoomMemberRepository.findByRoomIdAndMemberId(
                        room.getRoomId(),
                        member.getMemberId()
                );

        if (optionalMember.isPresent()) {

            ChatRoomMember existingMember =
                    optionalMember.get();

            if (Boolean.FALSE.equals(
                    existingMember.getIsLeft()
            )) {

                System.out.println(
                        "INVITE_LINK_JOIN_SKIPPED_ALREADY_JOINED memberId="
                                + member.getMemberId()
                                + ", roomId="
                                + room.getRoomId()
                );

                return;
            }

            existingMember.rejoin();

            existingMember.setLastReadAt(
                    null
            );

            chatRoomMemberRepository.save(
                    existingMember
            );

        } else {

            ChatRoomMember chatRoomMember =
                    new ChatRoomMember();

            chatRoomMember.setRoomId(
                    room.getRoomId()
            );

            chatRoomMember.setMemberId(
                    member.getMemberId()
            );

            chatRoomMember.setLastReadAt(
                    null
            );

            chatRoomMember.setIsLeft(
                    false
            );

            chatRoomMember.setLeftAt(
                    null
            );

            chatRoomMemberRepository.save(
                    chatRoomMember
            );
        }

        String nickname =
                member.getNickname() != null
                        ? member.getNickname()
                        : member.getEmailId();

        String systemContent =
                nickname
                        + "님이 초대 링크로 채팅방에 참여했습니다.";

        ChatMessage systemMessage =
                new ChatMessage();

        systemMessage.setRoomId(
                room.getRoomId()
        );

        systemMessage.setSenderId(
                member.getMemberId()
        );

        systemMessage.setSenderNickname(
                "SYSTEM"
        );

        systemMessage.setMessageContent(
                systemContent
        );

        systemMessage.setMessageType(
                "SYSTEM"
        );

        systemMessage.setFileUrl(
                null
        );

        ChatMessage savedMessage =
                chatMessageRepository.save(
                        systemMessage
                );

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + room.getRoomId(),
                savedMessage
        );

        broadcastRoomUpdateToMembers(
                room.getRoomId(),
                systemContent,
                "SYSTEM",
                savedMessage
        );
    }

    @Transactional
    public void inviteFriend(
            Long roomId,
            Long inviterId,
            Long friendId
    ) {

        ChatRoom room =
                chatRoomRepository.findById(
                                roomId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "채팅방이 존재하지 않습니다."
                                )
                        );

        Member inviter =
                memberRepository.findById(
                                inviterId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "초대한 사용자가 존재하지 않습니다."
                                )
                        );

        Member friend =
                memberRepository.findById(
                                friendId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자가 존재하지 않습니다."
                                )
                        );

        if (Boolean.TRUE.equals(
                friend.getRejectChat()
        )) {

            throw new RuntimeException(
                    "상대방은 채팅 초대 거부 상태입니다."
            );
        }

        boolean alreadyJoined =
                chatRoomMemberRepository
                        .existsByRoomIdAndMemberIdAndIsLeftFalse(
                                room.getRoomId(),
                                friend.getMemberId()
                        );

        if (alreadyJoined) {

            throw new RuntimeException(
                    "이미 참여중인 사용자입니다."
            );
        }

        Optional<ChatRoomMember> leftMember =
                chatRoomMemberRepository.findByRoomIdAndMemberId(
                        room.getRoomId(),
                        friend.getMemberId()
                );

        if (leftMember.isPresent()) {

            ChatRoomMember existingMember =
                    leftMember.get();

            existingMember.rejoin();

            existingMember.setLastReadAt(
                    null
            );

            chatRoomMemberRepository.save(
                    existingMember
            );

        } else {

            ChatRoomMember chatRoomMember =
                    new ChatRoomMember();

            chatRoomMember.setRoomId(
                    room.getRoomId()
            );

            chatRoomMember.setMemberId(
                    friend.getMemberId()
            );

            chatRoomMember.setLastReadAt(
                    null
            );

            chatRoomMember.setIsLeft(
                    false
            );

            chatRoomMember.setLeftAt(
                    null
            );

            chatRoomMemberRepository.save(
                    chatRoomMember
            );
        }

        String friendNickname =
                friend.getNickname() != null
                        ? friend.getNickname()
                        : friend.getEmailId();

        String systemContent =
                friendNickname
                        + "님이 채팅방에 초대되었습니다.";

        ChatMessage systemMessage =
                new ChatMessage();

        systemMessage.setRoomId(
                room.getRoomId()
        );

        systemMessage.setSenderId(
                inviter.getMemberId()
        );

        systemMessage.setSenderNickname(
                "SYSTEM"
        );

        systemMessage.setMessageContent(
                systemContent
        );

        systemMessage.setMessageType(
                "SYSTEM"
        );

        systemMessage.setFileUrl(
                null
        );

        ChatMessage savedMessage =
                chatMessageRepository.save(
                        systemMessage
                );

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + room.getRoomId(),
                savedMessage
        );

        broadcastRoomUpdateToMembers(
                room.getRoomId(),
                systemContent,
                "SYSTEM",
                savedMessage
        );

        String inviterNickname =
                inviter.getNickname() != null
                        ? inviter.getNickname()
                        : inviter.getEmailId();

        String notificationContent =
                inviterNickname
                        + "님이 채팅방에 초대했습니다.";

        notificationService.createNotification(
                friend.getEmailId(),
                inviter.getEmailId(),
                "CHAT_INVITE",
                room.getRoomId(),
                notificationContent
        );

        if (isViewingRoom(
                friend.getMemberId(),
                room.getRoomId()
        )) {

            return;
        }

        if (friend.getFcmToken() != null
                && !friend.getFcmToken()
                .trim()
                .isEmpty()) {

            fcmService.sendNotification(
                    friend.getFcmToken(),
                    "채팅 초대",
                    notificationContent
            );
        }
    }

    @Transactional
    public void leaveChatRoom(
            Long roomId,
            Long memberId
    ) {

        ChatRoom room =
                chatRoomRepository.findById(
                                roomId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "채팅방이 존재하지 않습니다."
                                )
                        );

        Member member =
                memberRepository.findById(
                                memberId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "사용자가 존재하지 않습니다."
                                )
                        );

        ChatRoomMember chatRoomMember =
                chatRoomMemberRepository.findByRoomIdAndMemberId(
                                room.getRoomId(),
                                member.getMemberId()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "채팅방 참여자가 아닙니다."
                                )
                        );

        if (Boolean.TRUE.equals(
                chatRoomMember.getIsLeft()
        )) {

            throw new RuntimeException(
                    "이미 나간 채팅방입니다."
            );
        }

        exitChatRoom(
                memberId,
                roomId
        );

        chatRoomMember.leave();

        chatRoomMemberRepository.save(
                chatRoomMember
        );

        String nickname =
                member.getNickname() != null
                        ? member.getNickname()
                        : member.getEmailId();

        String systemContent =
                nickname
                        + "님이 채팅방을 나갔습니다.";

        ChatMessage systemMessage =
                new ChatMessage();

        systemMessage.setRoomId(
                room.getRoomId()
        );

        systemMessage.setSenderId(
                member.getMemberId()
        );

        systemMessage.setSenderNickname(
                "SYSTEM"
        );

        systemMessage.setMessageContent(
                systemContent
        );

        systemMessage.setMessageType(
                "SYSTEM"
        );

        systemMessage.setFileUrl(
                null
        );

        ChatMessage savedMessage =
                chatMessageRepository.save(
                        systemMessage
                );

        messagingTemplate.convertAndSend(
                "/sub/chat/room/" + room.getRoomId(),
                savedMessage
        );

        broadcastRoomUpdateToMembers(
                room.getRoomId(),
                systemContent,
                "SYSTEM",
                savedMessage
        );
    }

    private void broadcastRoomUpdateToMembers(
            Long roomId,
            String lastMessage,
            String lastMessageType,
            ChatMessage savedMessage
    ) {

        if (roomId == null) {

            return;
        }

        List<ChatRoomMember> roomMembers =
                chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                        roomId
                );

        for (ChatRoomMember roomMember : roomMembers) {

            if (roomMember == null
                    || roomMember.getMemberId() == null) {

                continue;
            }

            ChatRoomUpdateDto roomUpdate =
                    new ChatRoomUpdateDto();

            roomUpdate.setRoomId(
                    roomId
            );

            roomUpdate.setLastMessage(
                    lastMessage
            );

            roomUpdate.setLastMessageType(
                    lastMessageType
            );

            if (savedMessage != null
                    && savedMessage.getSentAt() != null) {

                roomUpdate.setTime(
                        savedMessage.getSentAt()
                                .toString()
                );

            } else {

                roomUpdate.setTime(
                        ""
                );
            }

            roomUpdate.setTargetMemberId(
                    roomMember.getMemberId()
            );

            messagingTemplate.convertAndSend(
                    "/sub/chat/rooms/update/"
                            + roomMember.getMemberId(),
                    roomUpdate
            );
        }
    }
}