package com.example.backend.pay.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class PaymentPageDto {
    private final String hotelName;
    private final String roomName;
    private final LocalDate checkInDate;
    private final LocalDate checkoutDate; // <<-- 수정된 부분
    private final long nights;
    private final BigDecimal totalPrice;
    private final long reviewCount;
    private final double avgRating;

    public PaymentPageDto(String hotelName, String roomName, LocalDate checkInDate,
                          LocalDate checkoutDate, long nights, BigDecimal totalPrice, // <<-- 수정된 부분
                          long reviewCount, double avgRating) {
        this.hotelName = hotelName;
        this.roomName = roomName;
        this.checkInDate = checkInDate;
        this.checkoutDate = checkoutDate; // <<-- 수정된 부분
        this.nights = nights;
        this.totalPrice = totalPrice;
        this.reviewCount = reviewCount;
        this.avgRating = avgRating;
    }
}