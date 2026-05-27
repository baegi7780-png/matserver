package com.tech.motjip.repository;

import com.tech.motjip.domain.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    /*
     * 특정 장소의 후기 목록 조회
     */
    List<Review> findByPlaceIdOrderByCreatedAtDesc(
            Long placeId
    );

    /*
     * 특정 사용자가 작성한 후기 목록 조회
     */
    List<Review> findByMemberIdOrderByCreatedAtDesc(
            Long memberId
    );

    /*
     * 특정 장소 리뷰 개수
     */
    int countByPlaceId(
            Long placeId
    );

    /*
     * 특정 장소 평균 별점
     */
    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.placeId = :placeId
            """)
    Double getAverageRatingByPlaceId(
            @Param("placeId")
            Long placeId
    );

    /*
     * 특정 장소 최신 리뷰 조회
     */
    Review findTopByPlaceIdOrderByCreatedAtDesc(
            Long placeId
    );

    /*
     * 평균 평점이 기준 이상인 장소 ID 목록 조회
     * 예: 4.5 이상 추천 장소만 표시
     */
    @Query("""
            SELECT r.placeId
            FROM Review r
            GROUP BY r.placeId
            HAVING AVG(r.rating) >= :minRating
            """)
    List<Long> findPlaceIdsByMinimumRating(
            @Param("minRating")
            double minRating
    );

    /*
     * 특정 장소에서 기준 평점 이상을 준 리뷰 목록 조회
     * 여기서 랜덤으로 1명을 골라 프로필 이미지를 추천 마커에 사용
     */
    List<Review> findByPlaceIdAndRatingGreaterThanEqual(
            Long placeId,
            int rating
    );
}