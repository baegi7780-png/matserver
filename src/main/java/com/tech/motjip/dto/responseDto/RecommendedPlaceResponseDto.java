package com.tech.motjip.dto.responseDto;

public class RecommendedPlaceResponseDto {

    private Long placeId;

    private String placeName;

    private Double latitude;

    private Double longitude;

    /*
     * 소수점 첫째 자리까지 표시할 평균 평점
     * 예: 4.5, 4.7, 5.0
     */
    private Double averageRating;

    /*
     * 평점 4.5 이상을 준 사용자 중 랜덤 1명의 프로필 이미지
     */
    private String profileImageUrl;

    public RecommendedPlaceResponseDto(
            Long placeId,
            String placeName,
            Double latitude,
            Double longitude,
            Double averageRating,
            String profileImageUrl
    ) {
        this.placeId =
                placeId;
        this.placeName =
                placeName;
        this.latitude =
                latitude;
        this.longitude =
                longitude;
        this.averageRating =
                averageRating;
        this.profileImageUrl =
                profileImageUrl;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAverageRating() {
        return averageRating;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }
}