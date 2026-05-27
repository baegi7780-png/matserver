package com.tech.motjip.controller;

import com.tech.motjip.dto.responseDto.RecommendedPlaceResponseDto;
import com.tech.motjip.dto.responseDto.ReviewResponseDto;
import com.tech.motjip.service.ReviewService;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class ReviewController {

    private static final String TAG =
            "ReviewControllerDebug";

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService
    ) {
        this.reviewService =
                reviewService;
    }

    @PostMapping("/api/reviews")
    public ResponseEntity<Void> createReview(
            @RequestParam("placeId") Long placeId,
            @RequestParam(value = "placeName", required = false) String placeName,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam("memberId") Long memberId,
            @RequestParam("rating") int rating,
            @RequestParam("revisit") boolean revisit,
            @RequestParam("content") String content,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        System.out.println(TAG + " 후기 등록 요청");
        System.out.println(TAG + " placeId = " + placeId);
        System.out.println(TAG + " placeName = " + placeName);
        System.out.println(TAG + " latitude = " + latitude);
        System.out.println(TAG + " longitude = " + longitude);
        System.out.println(TAG + " memberId = " + memberId);

        reviewService.createReview(
                placeId,
                placeName,
                latitude,
                longitude,
                memberId,
                rating,
                revisit,
                content,
                tags,
                image
        );

        return ResponseEntity.ok().build();
    }

    @GetMapping("/api/reviews/{placeId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviews(
            @PathVariable Long placeId,
            @RequestParam("memberId") Long memberId
    ) {

        System.out.println(TAG + " 후기 목록 조회");
        System.out.println(TAG + " placeId = " + placeId);
        System.out.println(TAG + " currentMemberId = " + memberId);

        List<ReviewResponseDto> reviews =
                reviewService.getReviewsByPlaceId(
                        placeId,
                        memberId
                );

        return ResponseEntity.ok(
                reviews
        );
    }

    /*
     * 평균 평점 4.5 이상 추천 장소 조회
     *
     * 반환 데이터:
     * - 장소명
     * - 위도
     * - 경도
     * - 평균 평점(소수점 첫째 자리)
     * - 랜덤 사용자 프로필 이미지
     */
    @GetMapping("/api/reviews/recommended")
    public ResponseEntity<List<RecommendedPlaceResponseDto>>
    getRecommendedPlaces() {

        System.out.println(
                TAG + " 추천 장소 조회 요청"
        );

        List<RecommendedPlaceResponseDto> recommendedPlaces =
                reviewService.getRecommendedPlaces();

        return ResponseEntity.ok(
                recommendedPlaces
        );
    }

    @PatchMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable Long reviewId,
            @RequestParam(value = "placeName", required = false) String placeName,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam("memberId") Long memberId,
            @RequestParam("rating") int rating,
            @RequestParam("revisit") boolean revisit,
            @RequestParam("content") String content,
            @RequestParam(value = "tags", required = false) String tags,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {

        System.out.println(TAG + " 후기 수정 요청");
        System.out.println(TAG + " reviewId = " + reviewId);
        System.out.println(TAG + " placeName = " + placeName);
        System.out.println(TAG + " latitude = " + latitude);
        System.out.println(TAG + " longitude = " + longitude);
        System.out.println(TAG + " memberId = " + memberId);

        reviewService.updateReview(
                reviewId,
                placeName,
                latitude,
                longitude,
                memberId,
                rating,
                revisit,
                content,
                tags,
                image
        );

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam("memberId") Long memberId
    ) {

        System.out.println(TAG + " 후기 삭제 요청");
        System.out.println(TAG + " reviewId = " + reviewId);
        System.out.println(TAG + " memberId = " + memberId);

        reviewService.deleteReview(
                reviewId,
                memberId
        );

        return ResponseEntity.ok().build();
    }
}