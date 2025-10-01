package com.example.backend.feature.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 첫번쨰 메인 페이지
 * 여행에 빠지다 밑에 필요한 정보를 담는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PopularPackageDto {
    private Long id;
    private String title;
    private String cityName;
    private BigDecimal price;
    private String imageUrl;
}