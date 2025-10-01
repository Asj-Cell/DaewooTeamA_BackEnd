package com.example.backend.payment;

import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import com.example.backend.payment.entity.Payment;
import com.example.backend.payment.util.CardIssuerLookupUtil;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentResponseDto addPaymentMethod(Long userId, PaymentRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 카드 번호로 카드사 이름을 자동으로 판별
        String paymentName = CardIssuerLookupUtil.getIssuer(requestDto.getPaymentNumber());

        Payment newPayment = new Payment();
        newPayment.setUser(user);
        newPayment.setPaymentName(paymentName); // 자동으로 찾은 이름을 설정
        newPayment.setPaymentNumber(requestDto.getPaymentNumber());
        newPayment.setExpirationDate(requestDto.getExpirationDate());
        newPayment.setCvc(requestDto.getCvc());
        newPayment.setCardUser(requestDto.getCardUser());
        newPayment.setCountry(requestDto.getCountry());
        newPayment.setRegistrationDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(newPayment);

        return new PaymentResponseDto(savedPayment);
    }

    @Transactional
    public void deletePaymentMethod(Long paymentId, Long userId) {
        // 1. paymentId와 userId를 동시에 사용하여 본인의 결제 수단이 맞는지 확인
        Payment payment = paymentRepository.findByIdAndUserId(paymentId, userId)
                .orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없거나 존재하지 않는 결제 수단입니다."));

        // 2. 소유권이 확인되면 삭제 (Soft Delete)
        paymentRepository.delete(payment);
    }
}