package com.example.backend.travelPackage.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 첫번쨰 메인 페이지
 * 여행 더보기 밑에 필요한 정보를 담는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainPackageDto {
    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private List<String> imageUrls; // 모든 이미지 URL
}