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

    private final BigDecimal subtotal; // 방 가격 * 숙박일
    private final BigDecimal taxes; // 세금
    private final BigDecimal serviceFee; // 서비스 수수료
    private final BigDecimal totalPrice; // 최종 결제 금액

    private final long reviewCount;
    private final double avgRating;
}