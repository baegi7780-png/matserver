package com.tech.motjip.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "providers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Provider {

    @Id
    @Column(name = "provider_id")
    private Integer providerId;

    @Column(name = "provider", length = 50)
    private String providerName;

    @Builder
    public Provider(Integer providerId, String providerName) {
        this.providerId = providerId;
        this.providerName = providerName;
    }
}