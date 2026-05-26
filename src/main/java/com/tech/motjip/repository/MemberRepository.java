package com.tech.motjip.repository;

import com.tech.motjip.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByProviderIdAndEmailId(Integer providerId, String emailId);

    Optional<Member> findByEmailId(String emailId);

    // ✅ 위치 활성화된 사용자만 조회 (추천용)
    List<Member> findByLocationEnabledTrue();

    List<Member> findByMemberIdIn(
            List<Long> memberIds
    );
}