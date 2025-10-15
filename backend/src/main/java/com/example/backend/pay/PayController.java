package com.example.backend.pay;

import com.example.backend.pay.dto.FinalPaymentRequestDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
        //  try-catch 구문을 추가하여 서비스단에서 발생하는 예외를 처리합니다.
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            //  PayService의 메소드가 Exception을 던질 수 있으므로 try-catch로 감싸줍니다.
            Long reservationId = payService.processPaymentAndCreateReservation(requestDto, userId);
            //  성공 시 예약 ID와 200 OK 상태를 반환합니다.
            return ResponseEntity.ok(reservationId);
        } catch (Exception e) {
            //  실패 시 에러 메시지와 400 Bad Request 상태를 반환합니다.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            payService.cancelPaymentAndReservation(reservationId, requestDto.getCancelReason(), userId);
            return ResponseEntity.ok("예약 및 결제가 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}