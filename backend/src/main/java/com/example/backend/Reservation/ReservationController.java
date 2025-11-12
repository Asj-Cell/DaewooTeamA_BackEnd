package com.example.backend.Reservation;

import com.example.backend.pay.dto.PaymentPageDto;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final UserRepository userRepository; // <-- [수정] UserRepository 주입

    /**
     * 결제 페이지(예약 미리보기)에 필요한 상세 정보를 조회합니다.
     * 쿠폰 선택 시, 실시간으로 할인 금액을 계산하여 반환합니다.
     */
    @GetMapping("/preview")
    public ResponseEntity<PaymentPageDto> getPaymentPreview(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam(required = false) Long couponId, // <-- 2025-10-22 [수정] 쿠폰 ID를 파라미터로 받음
            @AuthenticationPrincipal UserDetails userDetails // <-- 2025-10-22 [수정] 사용자 인증 정보
    ) {
        User user = findUserFromUserDetails(userDetails);

        PaymentPageDto previewDetails = reservationService.getPaymentPreviewDetails(
                roomId,
                checkInDate,
                checkOutDate,
                couponId, // 2025-10-22 [수정] 서비스로 쿠폰 ID 전달
                user      // 2025-10-22 [수정] 서비스로 사용자 정보 전달

        );

        return ResponseEntity.ok(previewDetails);
    }

    /**
     * 2025-10-22 [추가] UserDetails에서 User 엔티티를 찾아오는 헬퍼 메소드
     */
    private User findUserFromUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            // 로그인을 확인하겠습니다
            return null;
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }
}
