package com.example.backend.pay;

import com.example.backend.Reservation.Reservation;
import com.example.backend.Reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@RequiredArgsConstructor
public class PayTransactionService {

    private final TossPaymentsService tossPaymentsService;
    private final ReservationRepository reservationRepository;
    private static final Logger log = LoggerFactory.getLogger(PayTransactionService.class);

    /**
     * [외부 API 호출 전용] 토스페이먼츠 결제 취소 API를 호출합니다.
     * 트랜잭션 없이 실행됩니다 (propagation = Propagation.NOT_SUPPORTED).
     */
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public boolean callTossCancelApi(String paymentKey, String cancelReason) {
        try {
            tossPaymentsService.cancelPayment(paymentKey, cancelReason);
            log.info("Successfully canceled payment on Toss for paymentKey: {}", paymentKey);
            return true;
        } catch (HttpClientErrorException e) {
            if (e.getResponseBodyAsString().contains("ALREADY_CANCELED_PAYMENT")) {
                log.warn("Payment for paymentKey {} was already canceled on Toss.", paymentKey);
                return true;
            }
            log.error("Toss API call failed for paymentKey {}: {}", paymentKey, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("An unexpected error occurred during Toss API call for paymentKey {}: {}", paymentKey, e.getMessage());
            return false;
        }
    }

    /**
     * [DB 업데이트 전용] 예약을 취소 상태로 변경합니다(소프트 삭제).
     * 새로운 트랜잭션에서 실행됩니다 (propagation = Propagation.REQUIRES_NEW).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReservationToCanceled(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("예약을 취소할 권한이 없습니다.");
        }
        if (!reservation.isDeleted()) {
            reservationRepository.delete(reservation); // @SQLDelete 어노테이션이 update 쿼리로 변경
            log.info("Reservation ID {} has been marked as canceled in the DB.", reservationId);
        } else {
            log.info("Reservation ID {} was already marked as canceled in the DB.", reservationId);
        }
    }
}