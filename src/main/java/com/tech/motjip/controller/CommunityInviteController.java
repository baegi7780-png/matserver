package com.tech.motjip.controller;

import com.tech.motjip.service.CommunityInviteService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/community-invites")
@RequiredArgsConstructor
public class CommunityInviteController {

    private final CommunityInviteService communityInviteService;

    @PostMapping("/{communityId}/{receiverId}")
    public ResponseEntity<Void> sendCommunityInvite(
            @PathVariable Long communityId,
            @PathVariable Long receiverId,
            Authentication authentication
    ) {

        String senderEmailId =
                authentication.getName();

        communityInviteService.sendCommunityInvite(
                senderEmailId,
                communityId,
                receiverId
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityInviteId}/respond")
    public ResponseEntity<Void> respondCommunityInvite(
            @PathVariable Long communityInviteId,
            @RequestParam String status,
            Authentication authentication
    ) {

        String receiverEmailId =
                authentication.getName();

        communityInviteService.respondCommunityInvite(
                receiverEmailId,
                communityInviteId,
                status
        );

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{communityInviteId}/cancel")
    public ResponseEntity<Void> cancelCommunityInvite(
            @PathVariable Long communityInviteId,
            Authentication authentication
    ) {

        String senderEmailId =
                authentication.getName();

        communityInviteService.cancelCommunityInvite(
                senderEmailId,
                communityInviteId
        );

        return ResponseEntity.ok().build();
    }
}