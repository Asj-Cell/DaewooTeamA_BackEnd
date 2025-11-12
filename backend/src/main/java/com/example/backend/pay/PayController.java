package com.example.backend.pay;

import com.example.backend.common.exception.MemberException;
import com.example.backend.pay.dto.FinalPaymentRequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.io.*;

@Slf4j
@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;

    /**
     * 결제 승인 및 예약 생성 요청
     */
    @PostMapping
    public ResponseEntity<?> processPaymentAndCreateReservation(@RequestBody FinalPaymentRequestDto requestDto,
                                                                @AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            throw MemberException.LOGIN_REQUIRED.getException();
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        Long reservationId = payService.processPaymentAndCreateReservation(requestDto, userId);
        return ResponseEntity.ok(reservationId);

    }

    /**
     * 결제 취소 요청 시 Body에 취소 사유를 담기 위한 DTO
     */
    @Getter
    @Setter
    public static class CancelRequestDto {
        private String cancelReason;
    }

    /**
     * 결제 및 예약 취소 요청
     */
    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelPaymentAndReservation(
            @PathVariable Long reservationId,
            @RequestBody CancelRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

            if(userDetails == null) {
                throw MemberException.LOGIN_REQUIRED.getException();
            }
            Long userId = Long.parseLong(userDetails.getUsername());
            payService.cancelPaymentAndReservation(reservationId, requestDto.getCancelReason(), userId);
            return ResponseEntity.ok("예약 및 결제가 성공적으로 취소되었습니다.");
    }
}
