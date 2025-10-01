package com.example.backend.review.dto;

import com.example.backend.review.entity.Review;
import lombok.Getter;

@Getter
public class ReviewResponseDto {
    private final String userName;
    private final String content;
    private final Double userRatingScore;
    private final String imageUrl; // 사용자 프로필 이미지 url ...

    public ReviewResponseDto(Review review) {
        this.userName = review.getUser().getUserName();
        this.content = review.getContent();
        this.userRatingScore = review.getUserRatingScore();
        this.imageUrl = review.getUser().getImageUrl();
    }
}