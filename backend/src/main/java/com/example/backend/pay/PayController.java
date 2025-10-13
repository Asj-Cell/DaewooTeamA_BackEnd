package com.example.backend.pay;

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

//    // ... 기존 결제 승인 메소드 ...
//    @PostMapping
//    public ResponseEntity<?> processPaymentAndCreateReservation(/* ... */) {
//        // ... 생략 ...
//    }


    // ✅ 이 클래스가 PayController 내부에 있는지 확인해주세요.
    @Getter
    @Setter
    public static class CancelRequestDto {
        private String cancelReason;
    }


    @PostMapping("/{reservationId}/cancel")
    public ResponseEntity<String> cancelPaymentAndReservation(
            @PathVariable Long reservationId,
            @RequestBody CancelRequestDto requestDto, // ✅ 이 DTO를 사용합니다.
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = Long.parseLong(userDetails.getUsername());
            // ✅ 이제 requestDto.getCancelReason()이 정상적으로 동작합니다.
            payService.cancelPaymentAndReservation(reservationId, requestDto.getCancelReason(), userId);
            return ResponseEntity.ok("예약 및 결제가 성공적으로 취소되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}