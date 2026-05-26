package com.tech.motjip.repository;

import com.tech.motjip.domain.NotificationType;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationTypeRepository extends JpaRepository<NotificationType, Long> {

    Optional<NotificationType> findByTypeName(String typeName);
}