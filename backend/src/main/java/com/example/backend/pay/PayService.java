package com.example.backend.pay;

import com.example.backend.Reservation.Reservation;
import com.example.backend.Reservation.ReservationRepository;
import com.example.backend.pay.dto.FinalPaymentRequestDto;
import com.example.backend.pay.entity.Pay;
import com.example.backend.room.RoomRepository;
import com.example.backend.room.entity.Room;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PayService {

    private final ReservationRepository reservationRepository;
    private final PayRepository payRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final TossPaymentsService tossPaymentsService;
    private final PayService self; // 자기 자신을 주입받기 위한 필드
    private static final Logger log = LoggerFactory.getLogger(PayService.class);

    // 자기 자신을 주입받기 위한 생성자 수정
    public PayService(ReservationRepository reservationRepository, PayRepository payRepository, UserRepository userRepository, RoomRepository roomRepository, TossPaymentsService tossPaymentsService, @Lazy PayService self) {
        this.reservationRepository = reservationRepository;
        this.payRepository = payRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.tossPaymentsService = tossPaymentsService;
        this.self = self;
    }


    @Transactional
    public Long processPaymentAndCreateReservation(FinalPaymentRequestDto requestDto, Long userId) throws Exception {
        // (기존 코드와 동일)
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(requestDto.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));

        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                requestDto.getRoomId(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate()
        );
        if (!overlappingReservations.isEmpty()) {
            throw new IllegalStateException("해당 날짜에 이미 예약된 방입니다.");
        }

        long nights = ChronoUnit.DAYS.between(requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        BigDecimal calculatedTotalPrice = room.getPrice().multiply(new BigDecimal(nights));
        if (calculatedTotalPrice.longValue() != requestDto.getAmount()) {
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
        Reservation savedReservation = reservationRepository.save(reservation);

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
     * 결제 및 예약 취소 로직 (트랜잭션 없음)
     * 이 메소드는 트랜잭션 흐름을 제어하는 역할만 합니다.
     */
    public void cancelPaymentAndReservation(Long reservationId, String cancelReason, Long userId) throws Exception {
        Pay pay = payRepository.findByReservation_Id(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 1. 외부 API 호출을 먼저 실행 (트랜잭션 없이)
        boolean isCancelSuccessOnToss = callTossCancelApi(pay.getPaymentKey(), cancelReason);

        // 2. 외부 API 호출이 성공했거나, 이미 취소된 상태일 경우에만
        //    별도의 트랜잭션으로 우리 DB 상태를 업데이트합니다.
        if (isCancelSuccessOnToss) {
            self.updateReservationToCanceled(reservationId, userId);
        }
    }

    /**
     * [외부 API 호출 전용] 토스페이먼츠 결제 취소 API를 호출합니다.
     * 트랜잭션과 무관하게 동작해야 하므로 propagation = Propagation.NOT_SUPPORTED 설정
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean callTossCancelApi(String paymentKey, String cancelReason) {
        try {
            tossPaymentsService.cancelPayment(paymentKey, cancelReason);
            log.info("Successfully canceled payment on Toss for paymentKey: {}", paymentKey);
            return true; // API 호출 성공
        } catch (HttpClientErrorException e) {
            // 4xx 에러 응답 body에 "ALREADY_CANCELED_PAYMENT"가 포함된 경우
            if (e.getResponseBodyAsString().contains("ALREADY_CANCELED_PAYMENT")) {
                log.warn("Payment for paymentKey {} was already canceled on Toss.", paymentKey);
                return true; // 이미 취소된 상태이므로 성공으로 간주
            }
            log.error("Toss API call failed for paymentKey {}: {}", paymentKey, e.getMessage());
            return false; // 그 외 다른 4xx 에러는 실패
        } catch (Exception e) {
            // 그 외 모든 예외는 실패
            log.error("An unexpected error occurred during Toss API call for paymentKey {}: {}", paymentKey, e.getMessage());
            return false;
        }
    }

    /**
     * [DB 업데이트 전용] 예약을 취소 상태로 변경합니다.
     * 새로운 트랜잭션에서 실행되어야 하므로 propagation = Propagation.REQUIRES_NEW 설정
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReservationToCanceled(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("예약을 취소할 권한이 없습니다.");
        }

        // isDeleted 플래그를 확인하여 중복 실행 방지
        if (!reservation.isDeleted()) {
            reservationRepository.delete(reservation);
            log.info("Reservation ID {} has been marked as canceled in the DB.", reservationId);
        } else {
            log.info("Reservation ID {} was already marked as canceled in the DB.", reservationId);
        }
    }
}