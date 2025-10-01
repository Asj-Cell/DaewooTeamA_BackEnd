package com.example.backend.feature.hotelfilters.dto;

import com.example.backend.user.dto.UserProfileRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * 사용자가 입력하는 필터의 종류 dto
 */
public class HotelFilterRequestDto {
    // 첫 페이지: 예약 가능 관련
    private String cityName;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer minAvailableRooms;

    // 기존 필터
    private Boolean breakfastIncluded;
    private Boolean freeParking;
    private Boolean freeWifi;
    private Boolean airportShuttlebus;
    private Boolean freeCancellation;

    private Boolean frontDesk24;
    private Boolean airConditioner;
    private Boolean fitnessCenter;
    private Boolean pool;

    private Integer minAvgRating;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;

    private String sortBy; // "rating" / "priceAsc" / "priceDesc"

    private UserProfileRequestDto loginUser;
}
