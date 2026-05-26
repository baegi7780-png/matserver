package com.tech.motjip.repository;

import com.tech.motjip.domain.Favorite;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    boolean existsByMember_MemberIdAndCommunity_ComId(
            Long memberId,
            Long comId
    );

    boolean existsByMember_EmailIdAndCommunity_ComId(
            String emailId,
            Long comId
    );

    Optional<Favorite> findByMember_MemberIdAndCommunity_ComId(
            Long memberId,
            Long comId
    );

    void deleteByMember_MemberIdAndCommunity_ComId(
            Long memberId,
            Long comId
    );

    List<Favorite> findAllByMember_EmailIdOrderByFavoriteIdDesc(
            String emailId
    );
}