package com.example.backend.review.dto;

import lombok.Getter;

@Getter
public class ReviewPageTotalInfoDto {
    private final double averageRating;
    private final long totalReviews;

    public ReviewPageTotalInfoDto(double averageRating, long totalReviews) {
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }
}