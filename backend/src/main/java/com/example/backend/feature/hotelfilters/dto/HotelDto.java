package com.example.backend.feature.hotelfilters.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 두번째 페이지에 화면에 보여줄 호텔 정보들
 * 별점과 찜 여부 보류
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HotelDto {
    private Long id;
    private String name;
    private String address;
    private Integer grade;
    private Integer amenitiesCount;
    private BigDecimal price;
    private Double rating;
    private List<String> imageUrls;
    private Boolean favoriteId;
    private Long reviewCount;
}
