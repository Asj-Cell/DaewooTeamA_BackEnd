package com.example.backend.pay;

import com.example.backend.Reservation.Reservation;
import com.example.backend.Reservation.ReservationRepository;
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
    public Long processPaymentAndCreateReservation(FinalPaymentRequestDto requestDto, Long userId) throws Exception {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        Room room = roomRepository.findById(requestDto.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + requestDto.getRoomId()));

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                requestDto.getRoomId(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate()
        );
        if (!overlappingReservations.isEmpty()) {
            throw new IllegalStateException("해당 날짜에 이미 예약된 방입니다.");
        }

        long nights = ChronoUnit.DAYS.between(requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        BigDecimal subtotal = room.getPrice().multiply(new BigDecimal(nights));
        BigDecimal taxes = subtotal.multiply(TAX_RATE);
        BigDecimal serviceFee = SERVICE_FEE;

        BigDecimal discount = BigDecimal.ZERO;
        Coupon usedCoupon = null;

        if (requestDto.getCouponId() != null) {
            usedCoupon = couponRepository.findById(requestDto.getCouponId())
                    .orElseThrow(() -> new EntityNotFoundException("Coupon not found with id: " + requestDto.getCouponId()));

            boolean isValid = usedCoupon.getUser().getId().equals(userId) &&
                    !usedCoupon.isUsed() &&
                    !usedCoupon.getExpiryDate().isBefore(LocalDate.now());

            if (isValid) {
                discount = usedCoupon.getDiscountAmount();
            } else {
                throw new IllegalStateException("유효하지 않은 쿠폰입니다.");
            }
        }

        BigDecimal calculatedTotalPrice = subtotal.add(taxes).add(serviceFee).subtract(discount);

        if (calculatedTotalPrice.longValue() != requestDto.getAmount()) {
            log.warn("결제 금액 불일치: [Request: {}], [Server Calculated: {}]", requestDto.getAmount(), calculatedTotalPrice.longValue());
            throw new IllegalStateException("요청된 결제 금액이 서버에서 계산된 금액과 일치하지 않습니다.");
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
    public void cancelPaymentAndReservation(Long reservationId, String cancelReason, Long userId) throws Exception {
        Pay pay = payRepository.findByReservation_Id(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // ⭐ 3. self 대신 payTransactionService 호출
        boolean isCancelSuccessOnToss = payTransactionService.callTossCancelApi(pay.getPaymentKey(), cancelReason);

        if (isCancelSuccessOnToss) {
            // ⭐ 4. self 대신 payTransactionService 호출
            payTransactionService.updateReservationToCanceled(reservationId, userId);
        }
    }

    // --- ⭐ 5. 아래 두 메소드는 PayTransactionService.java 로 완전히 이동되었으므로 여기서 삭제 ---
    // @Transactional(propagation = Propagation.NOT_SUPPORTED)
    // public boolean callTossCancelApi(String paymentKey, String cancelReason) { ... }

    // @Transactional(propagation = Propagation.REQUIRES_NEW)
    // public void updateReservationToCanceled(Long reservationId, Long userId) { ... }
}