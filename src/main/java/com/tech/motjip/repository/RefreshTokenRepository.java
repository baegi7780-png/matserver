package com.tech.motjip.repository;

import com.tech.motjip.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByEmail(String email);

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    void deleteByRefreshToken(String refreshToken);

    void deleteByEmail(String email);
}