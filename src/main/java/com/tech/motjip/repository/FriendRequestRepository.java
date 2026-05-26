package com.tech.motjip.repository;

import com.tech.motjip.domain.FriendRequest;
import com.tech.motjip.domain.FriendRequestStatus;
import com.tech.motjip.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FriendRequestRepository
        extends JpaRepository<FriendRequest, Long> {

    Optional<FriendRequest> findBySenderAndReceiver(
            Member sender,
            Member receiver
    );

    boolean existsBySenderAndReceiverAndStatusIn(
            Member sender,
            Member receiver,
            List<FriendRequestStatus> statusList
    );

    boolean existsByReceiverAndSenderAndStatusIn(
            Member receiver,
            Member sender,
            List<FriendRequestStatus> statusList
    );

    Optional<FriendRequest> findTopBySenderAndReceiverOrderByCreatedAtDesc(
            Member sender,
            Member receiver
    );

    Optional<FriendRequest> findTopByReceiverAndSenderOrderByCreatedAtDesc(
            Member receiver,
            Member sender
    );

    List<FriendRequest> findBySenderAndReceiverOrSenderAndReceiver(
            Member sender1,
            Member receiver1,
            Member sender2,
            Member receiver2
    );

    @Query("""
            SELECT fr
            FROM FriendRequest fr
            WHERE fr.status = :status
              AND (
                    (fr.sender = :member1 AND fr.receiver = :member2)
                    OR
                    (fr.sender = :member2 AND fr.receiver = :member1)
                  )
            """)
    List<FriendRequest> findBetweenMembersByStatus(
            @Param("member1") Member member1,
            @Param("member2") Member member2,
            @Param("status") FriendRequestStatus status
    );
}