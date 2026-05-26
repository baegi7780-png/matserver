package com.tech.motjip.repository;

import com.tech.motjip.domain.Community;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface CommunityRepository
        extends JpaRepository<Community, Long> {

    @Query("""
            SELECT c
            FROM Community c
            WHERE c.isDeleted = false
              AND c.meetingAt >= :now
              AND (:title IS NULL OR :title = ''
                   OR c.title LIKE CONCAT('%', :title, '%'))
              AND (:tag IS NULL OR :tag = ''
                   OR c.tag.tagName = :tag)
              AND (:region IS NULL OR :region = ''
                   OR c.region = :region)
              AND NOT EXISTS (
                   SELECT cm
                   FROM CommunityMember cm
                   WHERE cm.community = c
                     AND cm.member.emailId = :emailId
                     AND cm.status = 'KICKED'
              )
            """)
    Page<Community> searchCommunities(
            @Param("emailId") String emailId,
            @Param("title") String title,
            @Param("tag") String tag,
            @Param("region") String region,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
            SELECT c
            FROM Community c
            WHERE c.isDeleted = false
              AND c.meetingAt < :now
              AND (:title IS NULL OR :title = ''
                   OR c.title LIKE CONCAT('%', :title, '%'))
              AND (:tag IS NULL OR :tag = ''
                   OR c.tag.tagName = :tag)
              AND (:region IS NULL OR :region = ''
                   OR c.region = :region)
              AND NOT EXISTS (
                   SELECT cm
                   FROM CommunityMember cm
                   WHERE cm.community = c
                     AND cm.member.emailId = :emailId
                     AND cm.status = 'KICKED'
              )
            """)
    Page<Community> searchClosedCommunities(
            @Param("emailId") String emailId,
            @Param("title") String title,
            @Param("tag") String tag,
            @Param("region") String region,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
            SELECT c
            FROM Community c
            WHERE c.isDeleted = false
              AND c.meetingAt >= :now
              AND c.member.emailId = :emailId
            """)
    Page<Community> findMyCommunities(
            @Param("emailId") String emailId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
            SELECT c
            FROM Community c
            JOIN CommunityMember cm
              ON cm.community = c
            WHERE c.isDeleted = false
              AND c.meetingAt >= :now
              AND cm.member.emailId = :emailId
              AND cm.status = 'JOINED'
              AND (cm.hidden = false OR cm.hidden IS NULL)
            """)
    Page<Community> findMyJoinedCommunities(
            @Param("emailId") String emailId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
            SELECT c
            FROM Community c
            WHERE c.isDeleted = false
              AND c.meetingAt < :now
              AND c.member.emailId = :emailId
            """)
    Page<Community> findMyClosedCommunities(
            @Param("emailId") String emailId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );

    @Query("""
            SELECT c
            FROM Community c
            JOIN CommunityMember cm
              ON cm.community = c
            WHERE c.isDeleted = false
              AND c.meetingAt < :now
              AND cm.member.emailId = :emailId
              AND cm.status = 'JOINED'
              AND (cm.hidden = false OR cm.hidden IS NULL)
            """)
    Page<Community> findMyJoinedClosedCommunities(
            @Param("emailId") String emailId,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );
}