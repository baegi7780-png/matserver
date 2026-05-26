package com.tech.motjip.domain;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notification_types")
@Getter
@Setter
@NoArgsConstructor
public class NotificationType {

    @Id
    @Column(name = "noti_type_id")
    private Integer notiTypeId;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @Column(name = "description")
    private String description;
}