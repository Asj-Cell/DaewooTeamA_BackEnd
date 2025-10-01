package com.example.backend.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern; // import 추가!
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRequestDto {
    @NotBlank(message = "카드 번호를 입력해주세요.")
    private String paymentNumber;

    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/([0-9]{2})$", message = "유효 기간은 MM/YY 형식으로 입력해주세요.")
    @NotBlank(message = "유효 기간을 입력해주세요.")
    private String expirationDate;

    @NotBlank(message = "CVC를 입력해주세요.")
    private String cvc;

    @NotBlank(message = "카드 소유자 이름을 입력해주세요.")
    private String cardUser;

    @NotBlank(message = "국가/지역을 선택해주세요.")
    private String country;
}