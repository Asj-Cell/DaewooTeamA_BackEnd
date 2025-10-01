package com.example.backend.pay.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class FinalPaymentRequestDto {
    // 예약을 생성하기 위한 정보
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    // 결제 내역(Pay)을 생성하기 위한 정보
    private Long paymentId; // 사용자가 선택한 카드의 ID (Payment 엔티티의 ID)
}
