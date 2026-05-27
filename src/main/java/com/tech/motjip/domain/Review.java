package com.tech.motjip.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    @Column(name = "place_name")
    private String placeName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "revisit", nullable = false)
    private boolean revisit;

    @Column(
            name = "content",
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String content;

    @Column(name = "tags")
    private String tags;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now(
                        ZoneId.of("Asia/Seoul")
                );

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now(
                        ZoneId.of("Asia/Seoul")
                );
    }

    public Review() {
    }

    public void updateReview(
            int rating,
            boolean revisit,
            String content,
            String tags,
            String imageUrl
    ) {

        this.rating = rating;

        this.revisit = revisit;

        this.content = content;

        this.tags = tags;

        /*
         * 새 이미지가 넘어온 경우만 교체
         */
        if (imageUrl != null
                && !imageUrl.trim().isEmpty()) {

            this.imageUrl = imageUrl;
        }
    }

    public Long getReviewId() {
        return reviewId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(
            Long placeId
    ) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(
            String placeName
    ) {
        this.placeName = placeName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(
            Double latitude
    ) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(
            Double longitude
    ) {
        this.longitude = longitude;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(
            Long memberId
    ) {
        this.memberId = memberId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(
            int rating
    ) {
        this.rating = rating;
    }

    public boolean isRevisit() {
        return revisit;
    }

    public void setRevisit(
            boolean revisit
    ) {
        this.revisit = revisit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(
            String content
    ) {
        this.content = content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(
            String tags
    ) {
        this.tags = tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl
    ) {
        this.imageUrl = imageUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}