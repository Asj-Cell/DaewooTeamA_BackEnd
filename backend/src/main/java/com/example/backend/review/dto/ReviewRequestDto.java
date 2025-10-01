package com.example.backend.review.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReviewRequestDto {
    // private Long userId; // 이 필드를 삭제합니다.
    private String content;
    private Double userRatingScore;
}