package com.tech.motjip.controller;

import com.tech.motjip.dto.requestDto.DeleteNotificationRequestDto;
import com.tech.motjip.dto.responseDto.NotificationResponseDto;
import com.tech.motjip.service.NotificationService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getMyNotifications(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<NotificationResponseDto> notifications =
                notificationService.getMyNotifications(email);

        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        notificationService.markAsRead(
                notificationId,
                email
        );

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{notificationId}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long notificationId,
            @RequestParam String status,
            Authentication authentication
    ) {

        String email = authentication.getName();

        notificationService.updateStatus(
                notificationId,
                email,
                status
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        String email = authentication.getName();

        notificationService.deleteNotification(
                notificationId,
                email
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteNotifications(
            @RequestBody DeleteNotificationRequestDto requestDto,
            Authentication authentication
    ) {

        String email = authentication.getName();

        notificationService.deleteNotifications(
                requestDto.getNotificationIds(),
                email
        );

        return ResponseEntity.ok().build();
    }
}