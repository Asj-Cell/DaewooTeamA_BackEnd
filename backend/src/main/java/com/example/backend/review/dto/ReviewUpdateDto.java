package com.example.backend.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewUpdateDto {
    private String content;
    private Double userRatingScore;
}