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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayService {

    private final ReservationRepository reservationRepository;
    private final PayRepository payRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final TossPaymentsService tossPaymentsService; // ✅ 토스 서비스 주입

    @Transactional
    public Long processPaymentAndCreateReservation(FinalPaymentRequestDto requestDto, Long userId) throws Exception {
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

        // 백엔드에서 가격을 계산하여 클라이언트가 보낸 금액과 비교 (위변조 방지)
        long nights = ChronoUnit.DAYS.between(requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        BigDecimal calculatedTotalPrice = room.getPrice().multiply(new BigDecimal(nights));
        if (calculatedTotalPrice.longValue() != requestDto.getAmount()) {
            throw new IllegalStateException("요청된 결제 금액이 서버에서 계산된 금액과 일치하지 않습니다.");
        }

        // ✅ 토스페이먼츠 결제 승인 요청을 먼저 수행
        JSONObject tossPaymentResult = tossPaymentsService.confirmPayment(
                requestDto.getPaymentKey(),
                requestDto.getOrderId(),
                requestDto.getAmount()
        );

        // ✅ 결제 승인 성공 시에만 예약 및 결제 내역 저장
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
        // ✅ 응답받은 paymentKey를 DB에 저장
        pay.setPaymentKey((String) tossPaymentResult.get("paymentKey"));
        payRepository.save(pay);

        return savedReservation.getId();
    }

    // ✅ 결제 및 예약 취소 서비스 메소드 추가
    @Transactional
    public void cancelPaymentAndReservation(Long reservationId, String cancelReason, Long userId) throws Exception {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new SecurityException("예약을 취소할 권한이 없습니다.");
        }

        Pay pay = payRepository.findByReservation_Id(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다."));

        // 토스페이먼츠에 결제 취소 요청
        tossPaymentsService.cancelPayment(pay.getPaymentKey(), cancelReason);

        // 우리 DB의 예약을 (소프트) 삭제 처리
        reservationRepository.delete(reservation);
    }
}
}
