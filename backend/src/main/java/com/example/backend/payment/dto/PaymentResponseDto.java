package com.example.backend.payment.dto;

import com.example.backend.payment.entity.Payment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class PaymentResponseDto {
    private Long paymentId;
    private String paymentName;
    private String last4Digits;
    private String expirationDate;

    public PaymentResponseDto(Payment payment) {
        this.paymentId = payment.getId();
        this.paymentName = payment.getPaymentName();
        this.last4Digits = payment.getPaymentNumber().substring(payment.getPaymentNumber().length() - 4);
        this.expirationDate = payment.getExpirationDate().format(DateTimeFormatter.ofPattern("MM/yy"));
    }
}