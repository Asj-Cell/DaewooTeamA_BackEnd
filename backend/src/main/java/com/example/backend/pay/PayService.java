package com.example.backend.pay;

import com.example.backend.Reservation.Reservation;
import com.example.backend.Reservation.ReservationRepository;
import com.example.backend.pay.dto.FinalPaymentRequestDto;
import com.example.backend.payment.PaymentRepository;
import com.example.backend.payment.entity.Payment;
import com.example.backend.room.RoomRepository;
import com.example.backend.room.entity.Room;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
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
    private final PayRepository payRepository; // Pay 엔티티를 위한 Repository
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final PaymentRepository paymentRepository; // Payment(카드정보)를 위한 Repository

    @Transactional
    public Long processPaymentAndCreateReservation(FinalPaymentRequestDto requestDto, Long userId) {
        // 1. 필요한 엔티티 조회
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Room room = roomRepository.findById(requestDto.getRoomId()).orElseThrow(() -> new RuntimeException("Room not found"));
        // 사용자가 선택한 카드가 유효하고, 본인의 카드인지 확인
        Payment payment = paymentRepository.findByIdAndUserId(requestDto.getPaymentId(), userId)
                .orElseThrow(() -> new RuntimeException("Payment method not found or not authorized"));

        //겹치는 예약있으면 실행 X
        List<Reservation> overlappingReservations = reservationRepository.findOverlappingReservations(
                requestDto.getRoomId(),
                requestDto.getCheckInDate(),
                requestDto.getCheckOutDate()
        );
        if (!overlappingReservations.isEmpty()) {
            throw new IllegalStateException("해당 날짜에 이미 예약된 방입니다.");
        }

        // 2. 가격 계산
        long nights = ChronoUnit.DAYS.between(requestDto.getCheckInDate(), requestDto.getCheckOutDate());
        BigDecimal totalPrice = room.getPrice().multiply(new BigDecimal(nights));

        // 3. Reservation 엔티티 생성 및 저장
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setRoom(room);
        reservation.setCheckinDate(requestDto.getCheckInDate());
        reservation.setCheckoutDate(requestDto.getCheckOutDate());
        reservation.setTotalPrice(totalPrice);
        Reservation savedReservation = reservationRepository.save(reservation);

        // 4. 외부 결제 게이트웨이 연동 로직 (현재는 성공했다고 가정)
        // 예: PG사 API 호출, 성공 시 다음 로직 진행

        // 5. Pay 엔티티(결제 내역) 생성 및 저장
        Pay pay = new Pay();
        pay.setUser(user);
        pay.setReservation(savedReservation); // 방금 생성된 Reservation과 연결
        pay.setPayment(payment); // 사용자가 선택한 카드로 설정
        pay.setPrice(totalPrice);
        pay.setRedate(LocalDateTime.now());
        payRepository.save(pay);

        // 6. 생성된 예약 ID 반환
        return savedReservation.getId();
    }
}
