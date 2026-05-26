package com.tech.motjip.service;

import com.tech.motjip.domain.Member;
import com.tech.motjip.domain.Notification;
import com.tech.motjip.domain.NotificationType;
import com.tech.motjip.dto.responseDto.NotificationResponseDto;
import com.tech.motjip.repository.MemberRepository;
import com.tech.motjip.repository.NotificationRepository;
import com.tech.motjip.repository.NotificationTypeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository notificationTypeRepository;
    private final MemberRepository memberRepository;

    public void createNotification(
            String receiverEmail,
            String senderEmail,
            String typeName,
            Long targetId,
            String content
    ) {

        Member receiver = memberRepository.findByEmailId(receiverEmail)
                .orElseThrow(() -> new RuntimeException("알림 받을 사용자를 찾을 수 없습니다."));

        Member sender = memberRepository.findByEmailId(senderEmail)
                .orElseThrow(() -> new RuntimeException("알림 보낸 사용자를 찾을 수 없습니다."));

        NotificationType notificationType =
                notificationTypeRepository.findByTypeName(typeName)
                        .orElseThrow(() -> new RuntimeException("알림 타입을 찾을 수 없습니다."));

        Notification notification = new Notification();

        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setNotificationType(notificationType);
        notification.setTargetId(targetId);
        notification.setContent(content);
        notification.setStatus("PENDING");
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getMyNotifications(
            String email
    ) {

        Member receiver = memberRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        return notificationRepository
                .findByReceiverOrderByCreatedAtDesc(receiver)
                .stream()
                .map(notification -> NotificationResponseDto.builder()
                        .notificationId(notification.getNotiId())
                        .senderNickname(notification.getSender().getNickname())
                        .type(notification.getNotificationType().getTypeName())
                        .targetId(notification.getTargetId())
                        .message(notification.getContent())
                        .status(notification.getStatus())
                        .isRead(notification.isRead())
                        .createdAt(notification.getCreatedAt())
                        .build()
                )
                .toList();
    }

    public void markAsRead(
            Long notificationId,
            String email
    ) {

        Member receiver = memberRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().getMemberId()
                .equals(receiver.getMemberId())) {

            throw new RuntimeException("본인의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.setRead(true);
    }

    public void updateStatus(
            Long notificationId,
            String email,
            String status
    ) {

        Member receiver = memberRepository.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));

        if (!notification.getReceiver().getMemberId()
                .equals(receiver.getMemberId())) {

            throw new RuntimeException("본인의 알림만 상태 변경할 수 있습니다.");
        }

        notification.setStatus(status);
        notification.setRead(true);
    }

    public void updateStatusByTargetId(
            Long targetId,
            String typeName,
            String status
    ) {

        Notification notification =
                notificationRepository
                        .findByTargetIdAndNotificationType_TypeName(
                                targetId,
                                typeName
                        )
                        .orElseThrow(() ->
                                new RuntimeException("알림을 찾을 수 없습니다.")
                        );

        notification.setStatus(status);

        notification.setRead(true);
    }

    public void deleteNotification(
            Long notificationId,
            String email
    ) {

        Member receiver = memberRepository.findByEmailId(email)
                .orElseThrow(() ->
                        new RuntimeException("사용자를 찾을 수 없습니다.")
                );

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new RuntimeException("알림을 찾을 수 없습니다.")
                );

        if (!notification.getReceiver().getMemberId()
                .equals(receiver.getMemberId())) {

            throw new RuntimeException("본인의 알림만 삭제할 수 있습니다.");
        }

        notificationRepository.delete(notification);
    }

    public void deleteNotifications(
            List<Long> notificationIds,
            String email
    ) {

        Member receiver = memberRepository.findByEmailId(email)
                .orElseThrow(() ->
                        new RuntimeException("사용자를 찾을 수 없습니다.")
                );

        List<Notification> notifications =
                notificationRepository.findAllById(notificationIds);

        for (Notification notification : notifications) {

            if (!notification.getReceiver().getMemberId()
                    .equals(receiver.getMemberId())) {

                throw new RuntimeException("본인의 알림만 삭제할 수 있습니다.");
            }
        }

        notificationRepository.deleteAll(notifications);
    }
}