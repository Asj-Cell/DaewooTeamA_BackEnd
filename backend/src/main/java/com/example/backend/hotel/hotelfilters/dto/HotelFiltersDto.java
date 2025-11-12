package com.example.backend.hotel.hotelfilters.dto;

import com.querydsl.core.annotations.QueryProjection;
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
public class HotelFiltersDto {
    private final Long id;
    private final String name;
    private final String address;
    private final Integer grade;
    private final Integer amenitiesCount;
    private final BigDecimal price;
    private final Double rating;
    private final List<String> imageUrls;
    private final Boolean favoriteId;
    private final Long reviewCount;
    
    @QueryProjection
    public HotelFiltersDto(Long id, String name, String address, Integer grade, 
                         Integer amenitiesCount, BigDecimal price, Double rating,
                         List<String> imageUrls, Boolean favoriteId, Long reviewCount) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.grade = grade;
        this.amenitiesCount = amenitiesCount;
        this.price = price;
        this.rating = rating;
        this.imageUrls = imageUrls != null ? imageUrls : List.of();
        this.favoriteId = favoriteId != null && favoriteId;
        this.reviewCount = reviewCount;
    }
}
