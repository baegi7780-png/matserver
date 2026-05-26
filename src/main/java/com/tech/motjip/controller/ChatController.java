package com.tech.motjip.controller;

import com.tech.motjip.domain.ChatMessage;
import com.tech.motjip.domain.ChatRoom;
import com.tech.motjip.dto.requestDto.CreateRoomRequestDto;
import com.tech.motjip.dto.responseDto.ChatMessageResponseDto;
import com.tech.motjip.dto.responseDto.ChatRoomResponseDto;
import com.tech.motjip.dto.responseDto.FriendResponseDto;
import com.tech.motjip.dto.responseDto.ParticipantResponseDto;
import com.tech.motjip.service.ChatMessageService;
import com.tech.motjip.service.ChatReadService;
import com.tech.motjip.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final ChatReadService chatReadService;
    private final ChatService chatService;

    @GetMapping("/api/chat/my-rooms/{memberId}")
    public List<ChatRoomResponseDto> getMyRooms(
            @PathVariable Long memberId
    ) {

        return chatMessageService.getMyRooms(
                memberId
        );
    }

    @Transactional
    @DeleteMapping("/api/chat/message/{messageId}")
    public ResponseEntity<String> deleteMessage(
            @PathVariable Long messageId
    ) {

        chatMessageService.deleteMessage(
                messageId
        );

        return ResponseEntity.ok(
                "메시지 삭제 완료"
        );
    }

    @GetMapping("/api/chat/messages/{roomId}")
    public List<ChatMessageResponseDto> getChatMessages(
            @PathVariable Long roomId
    ) {

        return chatMessageService.getChatMessages(
                roomId
        );
    }

    @Transactional
    @PostMapping("/api/chat/rooms")
    public ChatRoom createRoom(
            @RequestBody CreateRoomRequestDto request
    ) {

        return chatMessageService.createRoom(
                request
        );
    }

    @GetMapping("/api/chat/invite/{inviteCode}")
    public ChatRoom getRoomByInviteCode(
            @PathVariable String inviteCode
    ) {

        return chatMessageService.getRoomByInviteCode(
                inviteCode
        );
    }

    @PostMapping("/api/chat/invite/{inviteCode}/join")
    public ResponseEntity<String> joinRoomByInviteCode(
            @PathVariable String inviteCode,
            @RequestParam Long memberId
    ) {

        chatService.joinRoomByInviteCode(
                inviteCode,
                memberId
        );

        return ResponseEntity.ok(
                "채팅방 참여 완료"
        );
    }

    @MessageMapping("/chat/message")
    public void sendMessage(
            ChatMessage message
    ) {

        chatMessageService.sendMessage(
                message
        );
    }

    @Transactional
    @PatchMapping("/api/chat/read/{roomId}/{memberId}")
    public ResponseEntity<Map<String, Object>> updateReadTime(
            @PathVariable Long roomId,
            @PathVariable Long memberId
    ) {

        chatReadService.updateReadTime(
                roomId,
                memberId
        );

        Map<String, Object> result =
                chatMessageService.markRoomMessagesAsRead(
                        roomId,
                        memberId
                );

        return ResponseEntity.ok(
                result
        );
    }

    @DeleteMapping("/api/chat/room/{roomId}/leave")
    public ResponseEntity<String> leaveRoom(
            @PathVariable Long roomId,
            @RequestParam Long memberId
    ) {

        chatService.leaveChatRoom(
                roomId,
                memberId
        );

        chatService.exitChatRoom(
                memberId,
                roomId
        );

        chatMessageService.deleteRoomIfEmpty(
                roomId
        );

        return ResponseEntity.ok(
                "채팅방 나가기 완료"
        );
    }

    @GetMapping("/api/chat/rooms/{roomId}/members")
    public List<ParticipantResponseDto> getRoomMembers(
            @PathVariable Long roomId
    ) {

        return chatMessageService.getRoomMembers(
                roomId
        );
    }

    @GetMapping("/api/chat/participants/{roomId}")
    public List<FriendResponseDto> getParticipants(
            @PathVariable Long roomId
    ) {

        return chatMessageService.getParticipants(
                roomId
        );
    }

    @PostMapping("/api/chat/rooms/{roomId}/invite/{friendId}")
    public ResponseEntity<String> inviteFriend(
            @PathVariable Long roomId,
            @PathVariable Long friendId,
            @RequestParam Long inviterId
    ) {

        chatService.inviteFriend(
                roomId,
                inviterId,
                friendId
        );

        return ResponseEntity.ok(
                "초대 완료"
        );
    }

    @PostMapping("/api/chat/enter")
    public ResponseEntity<Map<String, Object>> enterChatRoom(
            @RequestParam Long memberId,
            @RequestParam Long roomId
    ) {

        log.info(
                "ENTER_CHAT_ROOM_REQUEST memberId={}, roomId={}",
                memberId,
                roomId
        );

        chatService.enterChatRoom(
                memberId,
                roomId
        );

        Map<String, Object> result =
                chatMessageService.markRoomMessagesAsRead(
                        roomId,
                        memberId
                );

        return ResponseEntity.ok(
                result
        );
    }

    @PostMapping("/api/chat/exit")
    public ResponseEntity<Void> exitChatRoom(
            @RequestParam Long memberId,
            @RequestParam Long roomId
    ) {

        log.info(
                "EXIT_CHAT_ROOM_REQUEST memberId={}, roomId={}",
                memberId,
                roomId
        );

        chatService.exitChatRoom(
                memberId,
                roomId
        );

        return ResponseEntity.ok()
                .build();
    }
}