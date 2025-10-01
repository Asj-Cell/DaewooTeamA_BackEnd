package com.example.backend.pay;

import com.example.backend.pay.dto.FinalPaymentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pay")
@RequiredArgsConstructor
public class PayController {

    private final PayService payService;
    //헤더 키 Authorization -> 받은 토큰, Content-Type -> JSON
    @PostMapping
    public ResponseEntity<Long> processPaymentAndCreateReservation(@RequestBody FinalPaymentRequestDto requestDto,
                                                                   @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        Long reservationId = payService.processPaymentAndCreateReservation(requestDto, userId);
        return ResponseEntity.ok(reservationId);
    }
}
