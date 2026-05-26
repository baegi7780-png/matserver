package com.tech.motjip.controller;

import com.tech.motjip.dto.responseDto.FriendRecommendationResponseDto;
import com.tech.motjip.dto.responseDto.FriendStatusResponseDto;
import com.tech.motjip.service.FriendService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/friends")
@RequiredArgsConstructor
public class FriendController {

    private final FriendService friendService;

    @PostMapping("/requests/{receiverId}")
    public ResponseEntity<Void> sendFriendRequest(
            @PathVariable Long receiverId,
            Authentication authentication
    ) {

        String senderEmailId = authentication.getName();

        friendService.sendFriendRequest(
                senderEmailId,
                receiverId
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/requests/{friendRequestId}")
    public ResponseEntity<Void> respondFriendRequest(
            @PathVariable Long friendRequestId,
            @RequestParam String status,
            Authentication authentication
    ) {

        String receiverEmailId = authentication.getName();

        friendService.respondFriendRequest(
                receiverEmailId,
                friendRequestId,
                status
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/requests/{friendRequestId}/cancel")
    public ResponseEntity<Void> cancelFriendRequest(
            @PathVariable Long friendRequestId,
            Authentication authentication
    ) {

        String senderEmailId =
                authentication.getName();

        friendService.cancelFriendRequest(
                senderEmailId,
                friendRequestId
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/status/{targetMemberId}")
    public ResponseEntity<FriendStatusResponseDto> getFriendStatus(
            @PathVariable Long targetMemberId,
            Authentication authentication
    ) {

        String myEmailId =
                authentication.getName();

        String status =
                friendService.getFriendStatus(
                        myEmailId,
                        targetMemberId
                );

        return ResponseEntity.ok(
                new FriendStatusResponseDto(
                        status
                )
        );
    }

    @GetMapping("/recommendations")
    public ResponseEntity<List<FriendRecommendationResponseDto>> getFriendRecommendations(
            Authentication authentication
    ) {

        String myEmailId =
                authentication.getName();

        return ResponseEntity.ok(
                friendService.getFriendRecommendations(
                        myEmailId
                )
        );
    }

    @GetMapping
    public ResponseEntity<?> getMyFriends(
            Authentication authentication
    ) {

        String memberEmailId =
                authentication.getName();

        return ResponseEntity.ok(
                friendService.getMyFriends(
                        memberEmailId
                )
        );
    }

    @DeleteMapping("/{friendMemberId}")
    public ResponseEntity<Void> deleteFriend(
            @PathVariable Long friendMemberId,
            Authentication authentication
    ) {

        String memberEmailId = authentication.getName();

        friendService.deleteFriend(
                memberEmailId,
                friendMemberId
        );

        return ResponseEntity.ok().build();
    }
}