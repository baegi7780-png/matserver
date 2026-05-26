package com.tech.motjip.repository;

import com.tech.motjip.domain.Community;
import com.tech.motjip.domain.CommunityInvite;
import com.tech.motjip.domain.CommunityInviteStatus;
import com.tech.motjip.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityInviteRepository
        extends JpaRepository<CommunityInvite, Long> {

    boolean existsByCommunityAndSenderAndReceiverAndStatus(
            Community community,
            Member sender,
            Member receiver,
            CommunityInviteStatus status
    );

    Optional<CommunityInvite> findTopByCommunityAndSenderAndReceiverOrderByCreatedAtDesc(
            Community community,
            Member sender,
            Member receiver
    );

    List<CommunityInvite> findByReceiverAndStatus(
            Member receiver,
            CommunityInviteStatus status
    );
}