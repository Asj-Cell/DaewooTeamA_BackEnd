package com.example.backend.pay;

import com.example.backend.Reservation.Reservation;
import com.example.backend.Reservation.ReservationRepository;
import com.example.backend.common.exception.CouponException;
import com.example.backend.common.exception.HotelException;
import com.example.backend.common.exception.MemberException;
import com.example.backend.common.exception.PayException;
import com.example.backend.coupon.CouponRepository;
import com.example.backend.coupon.entity.Coupon;
import com.example.backend.pay.dto.FinalPaymentRequestDto;
import com.example.backend.pay.entity.Pay;
import com.example.backend.room.RoomRepository;
import com.example.backend.room.entity.Room;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor // 생성자 자동 생성
public class PayService {

    private final ReservationRepository reservationRepository;
    private final PayRepository payRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final TossPaymentsService tossPaymentsService;
    private final CouponRepository couponRepository;
    private final PayTransactionService payTransactionService;
    private static final Logger log = LoggerFactory.getLogger(PayService.class);

    private static final BigDecimal TAX_RATE = new BigDecimal("0.10");
    private static final BigDecimal SERVICE_FEE = new BigDecimal("5000");

    @Transactional
    public Long processPaymentAndCreateReservation(FinalPaymentRequestDto requestDto, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        MemberException.USER_NOT_FOUND.getException()
                );
        Room room = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() ->
                        HotelException.ROOM_NOT_FOUND.getException()
                );

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                requestDto.getRoomId(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate()
        );
        if (!overlappingReservations.isEmpty()) {
            throw HotelException.ROOM_ALREADY_BOOKED.getException();
        }

        long nights = ChronoUnit.DAYS.between(requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        BigDecimal subtotal = room.getPrice().multiply(new BigDecimal(nights));
        BigDecimal taxes = subtotal.multiply(TAX_RATE);
        BigDecimal serviceFee = SERVICE_FEE;

        BigDecimal discount = BigDecimal.ZERO;
        Coupon usedCoupon = null;

        if (requestDto.getCouponId() != null) {
            usedCoupon = couponRepository.findById(requestDto.getCouponId())
                    .orElseThrow(() ->
                            CouponException.COUPON_NOT_FOUND.getException()
                    );

            // 1. 소유주 검증
            if (!usedCoupon.getUser().getId().equals(userId)) {
                throw CouponException.COUPON_NOT_BELONG_TO_USER.getException();
            }
            // 2. 사용 여부 검증
            if (usedCoupon.isUsed()) {
                throw CouponException.COUPON_ALREADY_USED.getException();
            }
            // 3. 만료일 검증
            if (usedCoupon.getExpiryDate().isBefore(LocalDate.now())) {
                throw CouponException.COUPON_EXPIRED.getException();
            }
            discount = usedCoupon.getDiscountAmount();
        }

        BigDecimal calculatedTotalPrice = subtotal.add(taxes).add(serviceFee).subtract(discount);

        if (calculatedTotalPrice.longValue() != requestDto.getAmount()) {
            log.warn("결제 금액 불일치: [Request: {}], [Server Calculated: {}]", requestDto.getAmount(), calculatedTotalPrice.longValue());
            throw PayException.AMOUNT_DISCREPANCY.getException();
        }

        JSONObject tossPaymentResult = tossPaymentsService.confirmPayment(
                requestDto.getPaymentKey(),
                requestDto.getOrderId(),
                requestDto.getAmount()
        );

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setCheckinDate(requestDto.getCheckInDate());
        reservation.setCheckoutDate(requestDto.getCheckOutDate());
        reservation.setTotalPrice(calculatedTotalPrice);
        reservation.setTaxes(taxes);
        reservation.setServiceFee(serviceFee);
        reservation.setDiscount(discount);
        Reservation savedReservation = reservationRepository.save(reservation);

        if (usedCoupon != null) {
            usedCoupon.setUsed(true);
            usedCoupon.setReservation(savedReservation);
            couponRepository.save(usedCoupon);
        }

        Pay pay = new Pay();
        pay.setUser(user);
        pay.setReservation(savedReservation);
        pay.setPrice(calculatedTotalPrice);
        pay.setRedate(LocalDateTime.now());
        pay.setPaymentGateway("토스페이먼츠");
        pay.setPaymentKey((String) tossPaymentResult.get("paymentKey"));
        payRepository.save(pay);

        return savedReservation.getId();
    }


    /**
     * 결제 및 예약 취소 로직 (트랜잭션 흐름 제어 역할)
     */
    // [수정] 이 메소드에는 @Transactional 제거 (단순 흐름 제어만 하므로)
    public void cancelPaymentAndReservation(Long reservationId, String cancelReason, Long userId){
        Pay pay = payRepository.findByReservation_Id(reservationId)
                .orElseThrow(() ->
                        PayException.PAYMENT_INFORMATION_NOT_FOUND.getException()
                );

        // ⭐ 3. self 대신 payTransactionService 호출
        boolean isCancelSuccessOnToss = payTransactionService.callTossCancelApi(pay.getPaymentKey(), cancelReason);

        if (isCancelSuccessOnToss) {
            // ⭐ 4. self 대신 payTransactionService 호출
            payTransactionService.updateReservationToCanceled(reservationId, userId);
        }
    }
}