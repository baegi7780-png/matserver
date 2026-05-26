package com.tech.motjip.repository;

import com.tech.motjip.domain.Friend;
import com.tech.motjip.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository
        extends JpaRepository<Friend, Long> {

    boolean existsByMemberAndFriendMember(
            Member member,
            Member friendMember
    );

    List<Friend> findByMember(
            Member member
    );

    Optional<Friend> findByMemberAndFriendMember(
            Member member,
            Member friendMember
    );
}