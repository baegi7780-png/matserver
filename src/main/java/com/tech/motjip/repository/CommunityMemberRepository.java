package com.tech.motjip.repository;

import com.tech.motjip.domain.Community;
import com.tech.motjip.domain.CommunityMember;
import com.tech.motjip.domain.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityMemberRepository
        extends JpaRepository<CommunityMember, Long> {

    boolean existsByCommunityAndMember(
            Community community,
            Member member
    );

    boolean existsByCommunityAndMemberAndStatus(
            Community community,
            Member member,
            String status
    );

    boolean existsByCommunity_ComIdAndMember_EmailId(
            Long comId,
            String emailId
    );

    boolean existsByCommunity_ComIdAndMember_EmailIdAndStatus(
            Long comId,
            String emailId,
            String status
    );

    Optional<CommunityMember> findByCommunityAndMember(
            Community community,
            Member member
    );

    Optional<CommunityMember> findByCommunity_ComIdAndMember_MemberId(
            Long comId,
            Long memberId
    );

    Optional<CommunityMember> findByCommunity_ComIdAndMember_EmailId(
            Long comId,
            String emailId
    );

    Optional<CommunityMember> findByCommunity_ComIdAndMember_EmailIdAndStatus(
            Long comId,
            String emailId,
            String status
    );

    List<CommunityMember> findByCommunityAndStatus(
            Community community,
            String status
    );

    List<CommunityMember> findByMemberAndStatus(
            Member member,
            String status
    );

    List<CommunityMember> findByMemberAndStatusAndHiddenFalse(
            Member member,
            String status
    );

    int countByCommunity_ComIdAndStatus(
            Long comId,
            String status
    );
}