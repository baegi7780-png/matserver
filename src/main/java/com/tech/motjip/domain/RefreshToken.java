package com.tech.motjip.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(length = 500)
    private String refreshToken;

    private LocalDateTime expiryDate;

    public void updateToken(String newToken, LocalDateTime newExpiry) {
        this.refreshToken = newToken;
        this.expiryDate = newExpiry;
    }
}