package com.example.backend.hotel.hotelfilters.dto;

import com.example.backend.user.dto.UserProfileRequestDto;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 사용자가 입력하는 필터의 종류 dto
 */
public class HotelFilterRequestDto {
    // 첫 페이지: 예약 가능 관련
    private String cityName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate checkInDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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

//    private UserProfileRequestDto loginUser;
}
