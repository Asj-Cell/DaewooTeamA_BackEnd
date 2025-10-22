package com.example.backend.Reservation;

import com.example.backend.pay.dto.PaymentPageDto;
import com.example.backend.review.ReviewService;
import com.example.backend.review.dto.ReviewPageTotalInfoDto;
import com.example.backend.room.RoomRepository;
import com.example.backend.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final RoomRepository roomRepository;
    private final ReviewService reviewService; // 기존에 만들어둔 ReviewService 활용

    // 세금 비율 (10%)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    // 고정 서비스 수수료
    private static final BigDecimal SERVICE_FEE = new BigDecimal("5000");

    @Transactional(readOnly = true)
    public PaymentPageDto getPaymentPreviewDetails(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        ReviewPageTotalInfoDto reviewInfo = reviewService.getReviewTotalCountAndRating(room.getHotel().getId());

        // --- [S] 수정된 부분: 상세 금액 계산 ---
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        // 1. 소계 (방 가격 * 숙박일)
        BigDecimal subtotal = room.getPrice().multiply(new BigDecimal(nights));

        // 2. 세금 (소계의 10%)
        BigDecimal taxes = subtotal.multiply(TAX_RATE);

        // 3. 서비스 수수료 (고정값)
        BigDecimal serviceFee = SERVICE_FEE;

        // 4. 할인 (현재 0)
        BigDecimal discount = BigDecimal.ZERO; // 요청대로 일단 0으로 고정

        // 5. 최종 금액
        BigDecimal totalPrice = subtotal.add(taxes).add(serviceFee).subtract(discount);
        // --- [E] 수정된 부분 ---

        return PaymentPageDto.builder()
                .hotelName(room.getHotel().getName())
                .roomName(room.getName())
                .checkInDate(checkInDate)
                .checkoutDate(checkOutDate)
                .nights(nights)
                // --- [S] 수정된 부분: DTO에 상세 금액 전달 ---
                .subtotal(subtotal)
                .taxes(taxes)
                .serviceFee(serviceFee)
                .totalPrice(totalPrice)
                // --- [E] 수정된 부분 ---
                .reviewCount(reviewInfo.getTotalReviews())
                .avgRating(reviewInfo.getAverageRating())
                .build();
    }
}
