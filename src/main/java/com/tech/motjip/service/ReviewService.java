package com.tech.motjip.service;

import com.tech.motjip.domain.Member;
import com.tech.motjip.domain.Review;
import com.tech.motjip.dto.responseDto.RecommendedPlaceResponseDto;
import com.tech.motjip.dto.responseDto.ReviewResponseDto;
import com.tech.motjip.repository.MemberRepository;
import com.tech.motjip.repository.ReviewRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    private static final String REVIEW_IMAGE_DIR =
            System.getProperty("user.dir")
                    + File.separator
                    + "uploads"
                    + File.separator
                    + "reviews"
                    + File.separator;

    public ReviewService(
            ReviewRepository reviewRepository,
            MemberRepository memberRepository
    ) {
        this.reviewRepository =
                reviewRepository;
        this.memberRepository =
                memberRepository;
    }

    @Transactional
    public void createReview(
            Long placeId,
            String placeName,
            Double latitude,
            Double longitude,
            Long memberId,
            int rating,
            boolean revisit,
            String content,
            String tags,
            MultipartFile image
    ) {
        Review review =
                new Review();

        review.setPlaceId(placeId);
        review.setPlaceName(placeName);
        review.setLatitude(latitude);
        review.setLongitude(longitude);
        review.setMemberId(memberId);
        review.setRating(rating);
        review.setRevisit(revisit);
        review.setContent(content);
        review.setTags(tags);

        if (image != null
                && !image.isEmpty()) {
            review.setImageUrl(
                    saveReviewImage(image)
            );
        }

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByPlaceId(
            Long placeId,
            Long currentMemberId
    ) {
        List<Review> reviews =
                reviewRepository.findByPlaceIdOrderByCreatedAtDesc(
                        placeId
                );

        List<ReviewResponseDto> responseList =
                new ArrayList<>();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy년 MM월 dd일 HH시 mm분"
                );

        for (Review review : reviews) {
            ReviewResponseDto dto =
                    new ReviewResponseDto();

            dto.setReviewId(
                    review.getReviewId()
            );

            dto.setPlaceId(
                    review.getPlaceId()
            );

            dto.setMemberId(
                    review.getMemberId()
            );

            Member member =
                    null;

            if (review.getMemberId() != null) {
                member =
                        memberRepository.findById(
                                review.getMemberId()
                        ).orElse(null);
            }

            if (member != null
                    && member.getNickname() != null
                    && !member.getNickname().trim().isEmpty()) {
                dto.setNickname(
                        member.getNickname()
                );
            } else {
                dto.setNickname(
                        "사용자"
                );
            }

            if (member != null
                    && member.getProfileImgUrl() != null
                    && !member.getProfileImgUrl().trim().isEmpty()) {
                dto.setProfileImageUrl(
                        member.getProfileImgUrl()
                );
            } else {
                dto.setProfileImageUrl(
                        null
                );
            }

            dto.setMine(
                    currentMemberId != null
                            && review.getMemberId() != null
                            && currentMemberId.equals(
                            review.getMemberId()
                    )
            );

            dto.setRating(
                    review.getRating()
            );

            dto.setRevisit(
                    review.isRevisit()
            );

            dto.setContent(
                    review.getContent()
            );

            dto.setTags(
                    review.getTags()
            );

            dto.setImageUrl(
                    review.getImageUrl()
            );

            if (review.getCreatedAt() != null) {
                dto.setCreatedAt(
                        review.getCreatedAt().format(
                                formatter
                        )
                );
            } else {
                dto.setCreatedAt(
                        ""
                );
            }

            responseList.add(
                    dto
            );
        }

        return responseList;
    }

    /*
     * 평균 평점 4.5 이상 추천 장소 조회
     *
     * 조건:
     * 1. 장소 평균 평점이 4.5 이상
     * 2. 해당 장소에서 별점 5점을 준 리뷰 작성자 중 랜덤 1명 선택
     * 3. 지도에는 랜덤 사용자의 프로필 이미지와 평균 평점 표시
     * 4. 평균 평점은 소수점 첫째 자리까지 반올림
     */
    @Transactional(readOnly = true)
    public List<RecommendedPlaceResponseDto> getRecommendedPlaces() {

        List<Long> placeIds =
                reviewRepository.findPlaceIdsByMinimumRating(
                        4.5
                );

        List<RecommendedPlaceResponseDto> responseList =
                new ArrayList<>();

        for (Long placeId : placeIds) {

            List<Review> highRatingReviews =
                    reviewRepository.findByPlaceIdAndRatingGreaterThanEqual(
                            placeId,
                            5
                    );

            if (highRatingReviews == null
                    || highRatingReviews.isEmpty()) {
                continue;
            }

            Collections.shuffle(
                    highRatingReviews
            );

            Review randomReview =
                    highRatingReviews.get(0);

            Double averageRating =
                    reviewRepository.getAverageRatingByPlaceId(
                            placeId
                    );

            if (averageRating == null) {
                averageRating =
                        0.0;
            }

            averageRating =
                    Math.round(
                            averageRating * 10
                    ) / 10.0;

            String profileImageUrl =
                    null;

            if (randomReview.getMemberId() != null) {
                Member member =
                        memberRepository.findById(
                                randomReview.getMemberId()
                        ).orElse(null);

                if (member != null
                        && member.getProfileImgUrl() != null
                        && !member.getProfileImgUrl().trim().isEmpty()) {
                    profileImageUrl =
                            member.getProfileImgUrl();
                }
            }

            RecommendedPlaceResponseDto dto =
                    new RecommendedPlaceResponseDto(
                            randomReview.getPlaceId(),
                            randomReview.getPlaceName(),
                            randomReview.getLatitude(),
                            randomReview.getLongitude(),
                            averageRating,
                            profileImageUrl
                    );

            responseList.add(
                    dto
            );
        }

        return responseList;
    }

    @Transactional
    public void updateReview(
            Long reviewId,
            String placeName,
            Double latitude,
            Double longitude,
            Long memberId,
            int rating,
            boolean revisit,
            String content,
            String tags,
            MultipartFile image
    ) {
        Review review =
                reviewRepository.findById(
                        reviewId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "후기를 찾을 수 없습니다."
                        )
                );

        if (review.getMemberId() == null
                || !review.getMemberId().equals(memberId)) {
            throw new RuntimeException(
                    "수정 권한이 없습니다."
            );
        }

        if (placeName != null
                && !placeName.trim().isEmpty()) {
            review.setPlaceName(
                    placeName
            );
        }

        if (latitude != null) {
            review.setLatitude(
                    latitude
            );
        }

        if (longitude != null) {
            review.setLongitude(
                    longitude
            );
        }

        String imageUrl =
                null;

        if (image != null
                && !image.isEmpty()) {
            imageUrl =
                    saveReviewImage(
                            image
                    );
        }

        review.updateReview(
                rating,
                revisit,
                content,
                tags,
                imageUrl
        );
    }

    @Transactional
    public void deleteReview(
            Long reviewId,
            Long memberId
    ) {
        Review review =
                reviewRepository.findById(
                        reviewId
                ).orElseThrow(() ->
                        new RuntimeException(
                                "후기를 찾을 수 없습니다."
                        )
                );

        if (review.getMemberId() == null
                || !review.getMemberId().equals(memberId)) {
            throw new RuntimeException(
                    "삭제 권한이 없습니다."
            );
        }

        reviewRepository.delete(
                review
        );
    }

    private String saveReviewImage(
            MultipartFile image
    ) {
        try {
            File dir =
                    new File(
                            REVIEW_IMAGE_DIR
                    );

            if (!dir.exists()) {
                dir.mkdirs();
            }

            String originalFilename =
                    image.getOriginalFilename();

            String extension =
                    "";

            if (originalFilename != null
                    && originalFilename.contains(".")) {
                extension =
                        originalFilename.substring(
                                originalFilename.lastIndexOf(".")
                        );
            }

            String savedFileName =
                    UUID.randomUUID()
                            + extension;

            File dest =
                    new File(
                            dir,
                            savedFileName
                    );

            image.transferTo(
                    dest
            );

            return "/uploads/reviews/"
                    + savedFileName;

        } catch (IOException e) {
            throw new RuntimeException(
                    "후기 이미지 저장 실패",
                    e
            );
        }
    }
}