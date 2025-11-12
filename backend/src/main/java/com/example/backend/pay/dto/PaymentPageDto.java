package com.example.backend.pay.dto;

import com.example.backend.hotel.entity.HotelImage;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class PaymentPageDto {
    private final String hotelName;
    private final String roomName;
    private final LocalDate checkInDate;
    private final LocalDate checkoutDate; // <<-- 수정된 부분
    private final long nights;

    private final BigDecimal subtotal; // 방 가격 * 숙박일
    private final BigDecimal taxes; // 세금
    private final BigDecimal serviceFee; // 서비스 수수료
    private BigDecimal discount;
    private final BigDecimal totalPrice; // 최종 결제 금액

    private final long reviewCount;
    private final double avgRating;

    private final String cityName;
    private final String country;

    private final double latitude;
    private final double longitude;

    private final String view;
    private final String bed;

    private final List<String> hotelImagesUrl;
    private final String roomImageUrls;
}