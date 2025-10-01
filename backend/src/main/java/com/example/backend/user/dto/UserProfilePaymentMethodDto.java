package com.example.backend.user.dto;

import com.example.backend.payment.entity.Payment;
import lombok.Getter;

import java.time.format.DateTimeFormatter;

@Getter
public class UserProfilePaymentMethodDto {

    private final Long paymentId;
    private final String paymentName;
    private final String last4Digits;
    private final String expirationDate;

    public UserProfilePaymentMethodDto(Payment payment) {
        this.paymentId = payment.getId();
        this.paymentName = payment.getPaymentName();
        this.last4Digits = extractLast4Digits(payment.getPaymentNumber());

        if (payment.getExpirationDate() != null) {
            this.expirationDate = payment.getExpirationDate().format(DateTimeFormatter.ofPattern("MM/yy"));
        } else {
            this.expirationDate = "N/A";
        }
    }

    private String extractLast4Digits(String paymentNumber) {
        if (paymentNumber == null || paymentNumber.length() < 4) {
            return "****";
        }
        String digitsOnly = paymentNumber.replaceAll("[^0-9]", "");
        if (digitsOnly.length() < 4) {
            return "****";
        }
        return digitsOnly.substring(digitsOnly.length() - 4);
    }
}