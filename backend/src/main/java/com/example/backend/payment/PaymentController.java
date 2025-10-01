package com.example.backend.payment;

import com.example.backend.common.util.ApiResponse;
import com.example.backend.payment.dto.PaymentRequestDto;
import com.example.backend.payment.dto.PaymentResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments") // 기본 경로 변경
@RequiredArgsConstructor
@Tag(name = "Payment API", description = "✅ 인증 필요 | 결제 관련 API")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "내 결제 수단 추가", description = "로그인한 사용자의 결제 수단을 추가합니다.")
    @PostMapping("/me")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> addUserPaymentMethod(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentRequestDto requestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        PaymentResponseDto newPayment = paymentService.addPaymentMethod(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(newPayment));
    }

    @Operation(summary = "내 결제 수단 삭제", description = "로그인한 사용자의 특정 결제 수단을 삭제합니다.")
    @DeleteMapping("/me/{paymentId}")
    public ApiResponse<String> deleteUserPaymentMethod(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long paymentId) {
        Long userId = Long.parseLong(userDetails.getUsername());
        paymentService.deletePaymentMethod(paymentId, userId);
        return ApiResponse.success("결제 수단이 성공적으로 삭제되었습니다.");
    }
}