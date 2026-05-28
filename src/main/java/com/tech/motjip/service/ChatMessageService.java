package com.tech.motjip.service;

import com.tech.motjip.domain.ChatMessage;
import com.tech.motjip.domain.ChatMessageRead;
import com.tech.motjip.domain.ChatRoom;
import com.tech.motjip.domain.ChatRoomMember;
import com.tech.motjip.domain.Member;
import com.tech.motjip.dto.requestDto.CreateRoomRequestDto;
import com.tech.motjip.dto.responseDto.ChatMessageResponseDto;
import com.tech.motjip.dto.responseDto.ChatRoomResponseDto;
import com.tech.motjip.dto.responseDto.ChatRoomUpdateDto;
import com.tech.motjip.dto.responseDto.FriendResponseDto;
import com.tech.motjip.dto.responseDto.ParticipantResponseDto;
import com.tech.motjip.repository.ChatMessageReadRepository;
import com.tech.motjip.repository.ChatMessageRepository;
import com.tech.motjip.repository.ChatRoomMemberRepository;
import com.tech.motjip.repository.ChatRoomRepository;
import com.tech.motjip.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageReadRepository chatMessageReadRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final FcmService fcmService;

    @Value("${app.base-url}")
    private String baseUrl;

    public List<ChatRoomResponseDto> getMyRooms(
            Long memberId
    ) {

        List<ChatRoomMember> myRoomMembers =
                chatRoomMemberRepository.findByMemberIdAndIsLeftFalse(
                        memberId
                );

        if (myRoomMembers == null
                || myRoomMembers.isEmpty()) {

            return new ArrayList<>();
        }

        List<Long> roomIds =
                myRoomMembers.stream()
                        .map(ChatRoomMember::getRoomId)
                        .distinct()
                        .collect(Collectors.toList());

        Map<Long, ChatRoom> roomMap =
                chatRoomRepository.findByRoomIdIn(
                                roomIds
                        )
                        .stream()
                        .collect(Collectors.toMap(
                                ChatRoom::getRoomId,
                                room -> room
                        ));

        List<ChatMessage> latestMessages =
                chatMessageRepository.findLatestMessagesByRoomIds(
                        roomIds
                );

        Map<Long, ChatMessage> latestMessageMap =
                latestMessages.stream()
                        .collect(Collectors.toMap(
                                ChatMessage::getRoomId,
                                message -> message,
                                (oldValue, newValue) -> oldValue
                        ));

        List<ChatRoomMember> allRoomMembers =
                chatRoomMemberRepository.findByRoomIdInAndIsLeftFalse(
                        roomIds
                );

        Map<Long, Long> opponentIdMap =
                new HashMap<>();

        for (ChatRoomMember roomMember : allRoomMembers) {

            if (!roomMember.getMemberId()
                    .equals(memberId)) {

                opponentIdMap.putIfAbsent(
                        roomMember.getRoomId(),
                        roomMember.getMemberId()
                );
            }
        }

        List<Long> opponentIds =
                opponentIdMap.values()
                        .stream()
                        .distinct()
                        .collect(Collectors.toList());

        Map<Long, Member> opponentMap =
                new HashMap<>();

        if (!opponentIds.isEmpty()) {

            opponentMap =
                    memberRepository.findByMemberIdIn(
                                    opponentIds
                            )
                            .stream()
                            .collect(Collectors.toMap(
                                    Member::getMemberId,
                                    opponent -> opponent
                            ));
        }

        List<ChatRoomResponseDto> roomList =
                new ArrayList<>();

        for (ChatRoomMember myRoomMember : myRoomMembers) {

            ChatRoom room =
                    roomMap.get(
                            myRoomMember.getRoomId()
                    );

            if (room == null) {
                continue;
            }

            ChatMessage lastMessage =
                    latestMessageMap.get(
                            room.getRoomId()
                    );

            LocalDateTime lastMessageTime =
                    lastMessage != null
                            ? lastMessage.getSentAt()
                            : room.getCreatedAt();

            ChatRoomResponseDto dto =
                    new ChatRoomResponseDto();

            dto.setRoomId(
                    room.getRoomId()
            );

            dto.setRoomName(
                    room.getRoomName()
            );

            dto.setRoomType(
                    room.getRoomType()
            );

            dto.setLastMessageTime(
                    lastMessageTime
            );

            dto.setUnreadCount(
                    calculateRoomUnreadCountForMember(
                            room.getRoomId(),
                            memberId
                    )
            );

            if (lastMessage != null) {

                dto.setLastMessage(
                        lastMessage.getMessageContent()
                );

                dto.setLastMessageType(
                        lastMessage.getMessageType()
                );

                if (lastMessage.getSentAt() != null) {

                    dto.setTime(
                            lastMessage.getSentAt()
                                    .toString()
                    );

                } else {

                    dto.setTime(
                            ""
                    );
                }

            } else {

                dto.setLastMessage(
                        "아직 메시지가 없습니다."
                );

                dto.setLastMessageType(
                        "TEXT"
                );

                if (room.getCreatedAt() != null) {

                    dto.setTime(
                            room.getCreatedAt()
                                    .toString()
                    );

                } else {

                    dto.setTime(
                            ""
                    );
                }
            }

            if ("DIRECT".equals(
                    room.getRoomType()
            )) {

                Long opponentId =
                        opponentIdMap.get(
                                room.getRoomId()
                        );

                if (opponentId != null
                        && opponentMap.containsKey(
                        opponentId
                )) {

                    Member opponent =
                            opponentMap.get(
                                    opponentId
                            );

                    dto.setOpponentNickname(
                            opponent.getNickname()
                    );
                }
            }

            List<String> participantProfileImages =
                    new ArrayList<>();

            for (ChatRoomMember roomMember : allRoomMembers) {

                if (!roomMember.getRoomId()
                        .equals(room.getRoomId())) {

                    continue;
                }

                if (roomMember.getMemberId()
                        .equals(memberId)) {

                    continue;
                }

                memberRepository.findById(
                        roomMember.getMemberId()
                ).ifPresent(member -> {

                    participantProfileImages.add(
                            member.getProfileImgUrl()
                    );
                });

                if (participantProfileImages.size() >= 4) {

                    break;
                }
            }

            dto.setParticipantProfileImages(
                    participantProfileImages
            );

            if (room.getInviteCode() != null
                    && !room.getInviteCode()
                    .trim()
                    .isEmpty()) {

                dto.setInviteUrl(
                        baseUrl
                                + "/chat/invite/"
                                + room.getInviteCode()
                );
            }

            roomList.add(
                    dto
            );
        }

        roomList.sort((a, b) -> {

            if (a.getLastMessageTime() == null
                    && b.getLastMessageTime() == null) {

                return 0;
            }

            if (a.getLastMessageTime() == null) {

                return 1;
            }

            if (b.getLastMessageTime() == null) {

                return -1;
            }

            return b.getLastMessageTime()
                    .compareTo(
                            a.getLastMessageTime()
                    );
        });

        return roomList;
    }

    public List<ChatMessageResponseDto> getChatMessages(
            Long roomId
    ) {

        Pageable pageable =
                PageRequest.of(
                        0,
                        50
                );

        List<ChatMessage> recentMessages =
                chatMessageRepository.findByRoomIdOrderBySentAtDesc(
                        roomId,
                        pageable
                );

        List<ChatMessage> messages =
                new ArrayList<>(
                        recentMessages
                );

        messages.sort((a, b) ->
                a.getSentAt()
                        .compareTo(
                                b.getSentAt()
                        )
        );

        List<ChatRoomMember> roomMembers =
                chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                        roomId
                );

        int roomMemberCount =
                roomMembers.size();

        List<ChatMessageResponseDto> result =
                new ArrayList<>();

        for (ChatMessage message : messages) {

            result.add(
                    toChatMessageResponseDto(
                            message,
                            calculateMessageUnreadCount(
                                    message.getId(),
                                    roomMemberCount
                            )
                    )
            );
        }

        return result;
    }

    @Transactional
    public ChatRoom createRoom(
            CreateRoomRequestDto request
    ) {

        if (request.getMemberIds() == null
                || request.getMemberIds().isEmpty()) {

            throw new RuntimeException(
                    "채팅방 참여자가 없습니다."
            );
        }

        Set<Long> uniqueMemberIds =
                new LinkedHashSet<>();

        for (Long memberId : request.getMemberIds()) {

            if (memberId != null) {

                uniqueMemberIds.add(
                        memberId
                );
            }
        }

        if (uniqueMemberIds.isEmpty()) {

            throw new RuntimeException(
                    "유효한 채팅방 참여자가 없습니다."
            );
        }

        if ("DIRECT".equals(
                request.getRoomType()
        )
                && uniqueMemberIds.size() == 2) {

            List<Long> memberIds =
                    new ArrayList<>(
                            uniqueMemberIds
                    );

            List<ChatRoom> directRooms =
                    chatRoomRepository.findByRoomType(
                            "DIRECT"
                    );

            for (ChatRoom room : directRooms) {

                List<ChatRoomMember> members =
                        chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                                room.getRoomId()
                        );

                if (members.size() == 2) {

                    Long member1 =
                            memberIds.get(
                                    0
                            );

                    Long member2 =
                            memberIds.get(
                                    1
                            );

                    boolean hasMember1 =
                            false;

                    boolean hasMember2 =
                            false;

                    for (ChatRoomMember member : members) {

                        if (member.getMemberId()
                                .equals(member1)) {

                            hasMember1 =
                                    true;
                        }

                        if (member.getMemberId()
                                .equals(member2)) {

                            hasMember2 =
                                    true;
                        }
                    }

                    if (hasMember1
                            && hasMember2) {

                        if (room.getInviteCode() != null
                                && !room.getInviteCode()
                                .trim()
                                .isEmpty()) {

                            room.setInviteUrl(
                                    baseUrl
                                            + "/chat/invite/"
                                            + room.getInviteCode()
                            );
                        }

                        return room;
                    }
                }
            }
        }

        ChatRoom room =
                new ChatRoom();

        room.setRoomName(
                request.getRoomName()
        );

        room.setRoomType(
                request.getRoomType()
        );

        String inviteCode =
                UUID.randomUUID()
                        .toString()
                        .replace(
                                "-",
                                ""
                        );

        room.setInviteCode(
                inviteCode
        );

        ChatRoom savedRoom =
                chatRoomRepository.save(
                        room
                );

        savedRoom.setInviteUrl(
                baseUrl
                        + "/chat/invite/"
                        + room.getInviteCode()
        );

        for (Long memberId : uniqueMemberIds) {

            if (!memberRepository.existsById(
                    memberId
            )) {

                throw new RuntimeException(
                        "존재하지 않는 사용자입니다. memberId: "
                                + memberId
                );
            }

            boolean alreadyExists =
                    chatRoomMemberRepository
                            .findByRoomIdAndMemberId(
                                    savedRoom.getRoomId(),
                                    memberId
                            )
                            .isPresent();

            if (alreadyExists) {

                continue;
            }

            ChatRoomMember member =
                    new ChatRoomMember();

            member.setRoomId(
                    savedRoom.getRoomId()
            );

            member.setMemberId(
                    memberId
            );

            member.setLastReadAt(
                    null
            );

            member.setIsLeft(
                    false
            );

            member.setLeftAt(
                    null
            );

            chatRoomMemberRepository.save(
                    member
            );
        }

        return savedRoom;
    }

    @Transactional
    public void deleteMessage(
            Long messageId
    ) {

        ChatMessage message =
                chatMessageRepository.findById(
                                messageId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "메시지가 존재하지 않습니다."
                                )
                        );

        chatMessageRepository.delete(
                message
        );
    }

    @Transactional
    public void sendMessage(
            ChatMessage message
    ) {

        if (message.getMessageType() == null
                || message.getMessageType()
                .trim()
                .isEmpty()) {

            message.setMessageType(
                    "TEXT"
            );
        }

        if (message.getSenderId() != null) {

            memberRepository.findById(
                            message.getSenderId()
                    )
                    .ifPresent(member -> {

                        message.setSenderNickname(
                                member.getNickname()
                        );
                    });
        }

        ChatMessage savedMessage =
                chatMessageRepository.save(
                        message
                );

        List<ChatRoomMember> roomMembers =
                chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                        savedMessage.getRoomId()
                );

        saveReadIfAbsent(
                savedMessage.getId(),
                savedMessage.getRoomId(),
                savedMessage.getSenderId()
        );

        for (ChatRoomMember roomMember : roomMembers) {

            if (roomMember.getMemberId()
                    .equals(savedMessage.getSenderId())) {

                continue;
            }

            boolean isViewingRoom =
                    chatService.isViewingRoom(
                            roomMember.getMemberId(),
                            savedMessage.getRoomId()
                    );

            if (isViewingRoom) {

                saveReadIfAbsent(
                        savedMessage.getId(),
                        savedMessage.getRoomId(),
                        roomMember.getMemberId()
                );
            }
        }

        long unreadCount =
                calculateMessageUnreadCount(
                        savedMessage.getId(),
                        roomMembers.size()
                );

        ChatMessageResponseDto messageDto =
                toChatMessageResponseDto(
                        savedMessage,
                        unreadCount
                );

        messagingTemplate.convertAndSend(
                "/sub/chat/room/"
                        + savedMessage.getRoomId(),
                messageDto
        );

        broadcastRoomUpdatesForMembers(
                savedMessage,
                roomMembers
        );

        sendPushNotifications(
                savedMessage,
                roomMembers
        );
    }

    @Transactional
    public Map<String, Object> markRoomMessagesAsRead(
            Long roomId,
            Long memberId
    ) {

        Map<String, Object> result =
                new HashMap<>();

        if (roomId == null
                || memberId == null) {

            result.put(
                    "readMessageIds",
                    new ArrayList<Long>()
            );

            result.put(
                    "unreadCountMap",
                    new HashMap<String, Integer>()
            );

            return result;
        }

        List<ChatMessage> messages =
                chatMessageRepository.findByRoomIdOrderBySentAtAsc(
                        roomId
                );

        List<ChatRoomMember> roomMembers =
                chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                        roomId
                );

        int roomMemberCount =
                roomMembers.size();

        List<Long> readMessageIds =
                new ArrayList<>();

        Map<String, Integer> unreadCountMap =
                new HashMap<>();

        for (ChatMessage message : messages) {

            if (message == null
                    || message.getId() == null
                    || message.getSenderId() == null) {

                continue;
            }

            if (message.getSenderId()
                    .equals(memberId)) {

                continue;
            }

            boolean alreadyRead =
                    chatMessageReadRepository.existsByMessageIdAndMemberId(
                            message.getId(),
                            memberId
                    );

            if (alreadyRead) {

                continue;
            }

            saveReadIfAbsent(
                    message.getId(),
                    message.getRoomId(),
                    memberId
            );

            long unreadCount =
                    calculateMessageUnreadCount(
                            message.getId(),
                            roomMemberCount
                    );

            readMessageIds.add(
                    message.getId()
            );

            unreadCountMap.put(
                    String.valueOf(
                            message.getId()
                    ),
                    (int) unreadCount
            );
        }

        Map<String, Object> readPayload =
                new HashMap<>();

        readPayload.put(
                "type",
                "READ"
        );

        readPayload.put(
                "roomId",
                roomId
        );

        readPayload.put(
                "readerId",
                memberId
        );

        readPayload.put(
                "readMessageIds",
                readMessageIds
        );

        readPayload.put(
                "unreadCountMap",
                unreadCountMap
        );

        messagingTemplate.convertAndSend(
                "/sub/chat/room/"
                        + roomId
                        + "/read",
                readPayload
        );

        broadcastRoomUpdatesAfterRead(
                roomId,
                roomMembers
        );

        result.put(
                "readMessageIds",
                readMessageIds
        );

        result.put(
                "unreadCountMap",
                unreadCountMap
        );

        return result;
    }

    private void saveReadIfAbsent(
            Long messageId,
            Long roomId,
            Long memberId
    ) {

        if (messageId == null
                || roomId == null
                || memberId == null) {

            return;
        }

        boolean alreadyExists =
                chatMessageReadRepository.existsByMessageIdAndMemberId(
                        messageId,
                        memberId
                );

        if (alreadyExists) {

            return;
        }

        ChatMessageRead read =
                new ChatMessageRead();

        read.setMessageId(
                messageId
        );

        read.setRoomId(
                roomId
        );

        read.setMemberId(
                memberId
        );

        chatMessageReadRepository.save(
                read
        );
    }

    private long calculateMessageUnreadCount(
            Long messageId,
            int roomMemberCount
    ) {

        if (messageId == null) {

            return 0;
        }

        long readCount =
                chatMessageReadRepository.countByMessageId(
                        messageId
                );

        long unreadCount =
                roomMemberCount - readCount;

        return Math.max(
                unreadCount,
                0
        );
    }

    private long calculateRoomUnreadCountForMember(
            Long roomId,
            Long memberId
    ) {

        if (roomId == null
                || memberId == null) {

            return 0;
        }

        List<ChatMessage> messages =
                chatMessageRepository.findByRoomIdOrderBySentAtAsc(
                        roomId
                );

        List<ChatMessageRead> reads =
                chatMessageReadRepository.findByRoomIdAndMemberId(
                        roomId,
                        memberId
                );

        Set<Long> readMessageIdSet =
                reads.stream()
                        .map(ChatMessageRead::getMessageId)
                        .collect(Collectors.toSet());

        long unreadCount =
                0;

        for (ChatMessage message : messages) {

            if (message == null
                    || message.getId() == null
                    || message.getSenderId() == null) {

                continue;
            }

            if (message.getSenderId()
                    .equals(memberId)) {

                continue;
            }

            if (!readMessageIdSet.contains(
                    message.getId()
            )) {

                unreadCount++;
            }
        }

        return unreadCount;
    }

    private void broadcastRoomUpdatesForMembers(
            ChatMessage savedMessage,
            List<ChatRoomMember> roomMembers
    ) {

        for (ChatRoomMember roomMember : roomMembers) {

            ChatRoomUpdateDto roomUpdate =
                    createRoomUpdateDto(
                            savedMessage,
                            roomMember.getMemberId()
                    );

            messagingTemplate.convertAndSend(
                    "/sub/chat/rooms/update/"
                            + roomMember.getMemberId(),
                    roomUpdate
            );
        }
    }

    private void broadcastRoomUpdatesAfterRead(
            Long roomId,
            List<ChatRoomMember> roomMembers
    ) {

        ChatMessage lastMessage =
                chatMessageRepository.findTopByRoomIdOrderBySentAtDesc(
                        roomId
                );

        if (lastMessage == null) {

            return;
        }

        for (ChatRoomMember roomMember : roomMembers) {

            ChatRoomUpdateDto roomUpdate =
                    createRoomUpdateDto(
                            lastMessage,
                            roomMember.getMemberId()
                    );

            messagingTemplate.convertAndSend(
                    "/sub/chat/rooms/update/"
                            + roomMember.getMemberId(),
                    roomUpdate
            );
        }
    }

    private ChatRoomUpdateDto createRoomUpdateDto(
            ChatMessage message,
            Long targetMemberId
    ) {

        ChatRoomUpdateDto roomUpdate =
                new ChatRoomUpdateDto();

        roomUpdate.setRoomId(
                message.getRoomId()
        );

        roomUpdate.setLastMessage(
                message.getMessageContent()
        );

        roomUpdate.setLastMessageType(
                message.getMessageType()
        );

        if (message.getSentAt() != null) {

            roomUpdate.setTime(
                    message.getSentAt()
                            .toString()
            );

        } else {

            roomUpdate.setTime(
                    ""
            );
        }

        roomUpdate.setUnreadCount(
                (int) calculateRoomUnreadCountForMember(
                        message.getRoomId(),
                        targetMemberId
                )
        );

        roomUpdate.setTargetMemberId(
                targetMemberId
        );

        return roomUpdate;
    }

    private void sendPushNotifications(
            ChatMessage savedMessage,
            List<ChatRoomMember> roomMembers
    ) {

        if ("SYSTEM".equals(
                savedMessage.getMessageType()
        )) {

            return;
        }

        ChatRoom room =
                chatRoomRepository.findById(
                        savedMessage.getRoomId()
                ).orElse(null);

        String roomName =
                room != null
                        ? room.getRoomName()
                        : null;

        String roomType =
                room != null
                        ? room.getRoomType()
                        : null;

        for (ChatRoomMember roomMember : roomMembers) {

            if (roomMember.getMemberId()
                    .equals(savedMessage.getSenderId())) {

                continue;
            }

            memberRepository.findById(
                            roomMember.getMemberId()
                    )
                    .ifPresent(member -> {

                        if (chatService.isViewingRoom(
                                member.getMemberId(),
                                savedMessage.getRoomId()
                        )) {

                            return;
                        }

                        if (member.getFcmToken() == null
                                || member.getFcmToken()
                                .trim()
                                .isEmpty()) {

                            return;
                        }

                        String pushBody;

                        if ("IMAGE".equals(
                                savedMessage.getMessageType()
                        )) {

                            pushBody =
                                    "📷 사진";

                        } else {

                            pushBody =
                                    savedMessage.getMessageContent();
                        }

                        fcmService.sendNotification(
                                member.getFcmToken(),
                                savedMessage.getSenderNickname(),
                                pushBody,
                                savedMessage.getRoomId(),
                                roomName,
                                roomType,
                                "CHAT_MESSAGE"
                        );
                    });
        }
    }

    public void deleteRoomIfEmpty(
            Long roomId
    ) {

        long remainCount =
                chatRoomMemberRepository.countByRoomIdAndIsLeftFalse(
                        roomId
                );

        if (remainCount == 0) {

            chatMessageRepository.deleteByRoomId(
                    roomId
            );

            chatRoomRepository.deleteById(
                    roomId
            );
        }
    }

    public ChatRoom getRoomByInviteCode(
            String inviteCode
    ) {

        if (inviteCode == null
                || inviteCode.trim().isEmpty()) {

            throw new RuntimeException(
                    "초대 코드가 없습니다."
            );
        }

        ChatRoom room =
                chatRoomRepository.findByInviteCode(
                                inviteCode
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "유효하지 않은 초대 링크입니다."
                                )
                        );

        room.setInviteUrl(
                baseUrl
                        + "/chat/invite/"
                        + room.getInviteCode()
        );

        return room;
    }

    @Transactional
    public void joinRoomByInviteCode(
            String inviteCode,
            Long memberId
    ) {

        if (inviteCode == null
                || inviteCode.trim().isEmpty()) {

            throw new RuntimeException(
                    "초대 코드가 없습니다."
            );
        }

        if (memberId == null
                || memberId <= 0) {

            throw new RuntimeException(
                    "사용자 정보가 없습니다."
            );
        }

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
                nickname + "님이 초대 링크로 채팅방에 참여했습니다."
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
                toChatMessageResponseDto(
                        savedMessage,
                        0L
                )
        );

        List<ChatRoomMember> roomMembers =
                chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                        room.getRoomId()
                );

        broadcastRoomUpdatesForMembers(
                savedMessage,
                roomMembers
        );
    }

    public List<ParticipantResponseDto> getRoomMembers(
            Long roomId
    ) {

        List<ParticipantResponseDto> result =
                new ArrayList<>();

        List<ChatRoomMember> members =
                chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                        roomId
                );

        for (ChatRoomMember roomMember : members) {

            memberRepository.findById(
                            roomMember.getMemberId()
                    )
                    .ifPresent(member -> {

                        ParticipantResponseDto dto =
                                new ParticipantResponseDto();

                        dto.setMemberId(
                                member.getMemberId()
                        );

                        dto.setNickname(
                                member.getNickname()
                        );

                        dto.setProfileImgUrl(
                                member.getProfileImgUrl()
                        );

                        result.add(
                                dto
                        );
                    });
        }

        return result;
    }

    public List<FriendResponseDto> getParticipants(
            Long roomId
    ) {

        List<ChatRoomMember> members =
                chatRoomMemberRepository.findByRoomIdAndIsLeftFalse(
                        roomId
                );

        List<FriendResponseDto> result =
                new ArrayList<>();

        for (ChatRoomMember member : members) {

            memberRepository.findById(
                            member.getMemberId()
                    )
                    .ifPresent(m -> {

                        result.add(
                                new FriendResponseDto(
                                        m.getMemberId(),
                                        m.getNickname(),
                                        m.getProfileImgUrl()
                                )
                        );
                    });
        }

        return result;
    }

    private ChatMessageResponseDto toChatMessageResponseDto(
            ChatMessage message,
            Long unreadCount
    ) {

        ChatMessageResponseDto dto =
                new ChatMessageResponseDto();

        dto.setId(
                message.getId()
        );

        dto.setRoomId(
                message.getRoomId()
        );

        dto.setSenderId(
                message.getSenderId()
        );

        dto.setSenderNickname(
                message.getSenderNickname()
        );

        memberRepository.findById(
                        message.getSenderId()
                )
                .ifPresent(sender -> {

                    dto.setSenderProfileImage(
                            sender.getProfileImgUrl()
                    );
                });

        dto.setMessageContent(
                message.getMessageContent()
        );

        dto.setMessageType(
                message.getMessageType()
        );

        dto.setFileUrl(
                message.getFileUrl()
        );

        if (message.getSentAt() != null) {

            dto.setSentAt(
                    message.getSentAt()
                            .toString()
            );
        }

        if (unreadCount == null) {

            dto.setUnreadCount(
                    0
            );

        } else {

            dto.setUnreadCount(
                    unreadCount.intValue()
            );
        }

        return dto;
    }
}