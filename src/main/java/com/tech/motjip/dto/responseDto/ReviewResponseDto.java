package com.tech.motjip.dto.responseDto;

public class ReviewResponseDto {

    private Long reviewId;
    private Long placeId;
    private Long memberId;

    private String nickname;
    private String profileImageUrl;

    private boolean mine;

    private int rating;
    private boolean revisit;

    private String content;
    private String tags;
    private String imageUrl;

    private String createdAt;

    public Long getReviewId() {
        return reviewId;
    }

    public void setReviewId(
            Long reviewId
    ) {
        this.reviewId =
                reviewId;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(
            Long placeId
    ) {
        this.placeId =
                placeId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(
            Long memberId
    ) {
        this.memberId =
                memberId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(
            String nickname
    ) {
        this.nickname =
                nickname;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(
            String profileImageUrl
    ) {
        this.profileImageUrl =
                profileImageUrl;
    }

    public boolean isMine() {
        return mine;
    }

    public void setMine(
            boolean mine
    ) {
        this.mine =
                mine;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(
            int rating
    ) {
        this.rating =
                rating;
    }

    public boolean isRevisit() {
        return revisit;
    }

    public void setRevisit(
            boolean revisit
    ) {
        this.revisit =
                revisit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(
            String content
    ) {
        this.content =
                content;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(
            String tags
    ) {
        this.tags =
                tags;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(
            String imageUrl
    ) {
        this.imageUrl =
                imageUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            String createdAt
    ) {
        this.createdAt =
                createdAt;
    }
}