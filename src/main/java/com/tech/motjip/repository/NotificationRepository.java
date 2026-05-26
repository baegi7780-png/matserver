package com.tech.motjip.repository;

import com.tech.motjip.domain.Notification;
import com.tech.motjip.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverOrderByCreatedAtDesc(Member receiver);
    Optional<Notification> findByTargetIdAndNotificationType_TypeName(
            Long targetId,
            String typeName
    );
}