package com.example.backend.payment;

import com.example.backend.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 사용자 ID로 모든 결제 수단을 찾는 쿼리 메서드
    List<Payment> findAllByUserId(Long userId);

    // ID와 사용자 ID로 결제 수단을 찾는 쿼리 메서드 (삭제 시 소유권 확인용)
    Optional<Payment> findByIdAndUserId(Long paymentId, Long userId);



}