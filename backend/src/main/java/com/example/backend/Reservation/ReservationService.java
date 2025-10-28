package com.example.backend.Reservation;

import com.example.backend.coupon.CouponRepository;
import com.example.backend.coupon.entity.Coupon;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.user.entity.User;
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
    private final ReviewService reviewService;
    private final CouponRepository couponRepository; // <-- 2025-10-22 [수정] CouponRepository 주입

    // 세금 비율 (10%)
    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    // 고정 서비스 수수료
    private static final BigDecimal SERVICE_FEE = new BigDecimal("5000");

    @Transactional(readOnly = true)
    public PaymentPageDto getPaymentPreviewDetails(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, Long couponId, User user) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        ReviewPageTotalInfoDto reviewInfo = reviewService.getReviewTotalCountAndRating(room.getHotel().getId());

        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        BigDecimal subtotal = room.getPrice().multiply(new BigDecimal(nights));
        BigDecimal taxes = subtotal.multiply(TAX_RATE);
        BigDecimal serviceFee = SERVICE_FEE;


        BigDecimal discount = BigDecimal.ZERO;

        // 사용자가 로그인했고(user != null), 쿠폰 ID를 넘겼다면(couponId != null)
        if (user != null && couponId != null) {
            // 쿠폰을 조회하고 검증합니다.
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + couponId));

            // 쿠폰이 이 사용자의 것이고 유효한지 검사
            if (isCouponValid(coupon, user)) {
                discount = coupon.getDiscountAmount();
            }
        }

        // 5. 최종 금액
        BigDecimal totalPrice = subtotal.add(taxes).add(serviceFee).subtract(discount);


        return PaymentPageDto.builder()
                .hotelName(room.getHotel().getName())
                .roomName(room.getName())
                .checkInDate(checkInDate)
                .checkoutDate(checkOutDate)
                .nights(nights)
                .subtotal(subtotal)
                .taxes(taxes)
                .serviceFee(serviceFee)
                .discount(discount) // <-- 2025-10-22 [수정] DTO에 할인 금액 전달
                .totalPrice(totalPrice)
                .reviewCount(reviewInfo.getTotalReviews())
                .avgRating(reviewInfo.getAverageRating())
                .cityName(room.getHotel().getCity().getCityName())
                .country(room.getHotel().getCity().getCountry())
                .latitude(room.getHotel().getLatitude())
                .longitude(room.getHotel().getLongitude())
                .view(room.getView())
                .bed(room.getBed())
                .build();
    }

    /**
     * 2025-10-22 [추가] 쿠폰이 이 사용자가 사용 가능한지 검증하는 헬퍼 메소드
     */
    private boolean isCouponValid(Coupon coupon, User user) {
        if (coupon == null || user == null) {
            return false;
        }
        // 2025-10-22 [추가] 1. 소유자 확인, 2. 사용 여부 확인, 3. 만료일 확인
        return coupon.getUser().getId().equals(user.getId()) &&
                !coupon.isUsed() &&
                !coupon.getExpiryDate().isBefore(LocalDate.now());
    }
}
