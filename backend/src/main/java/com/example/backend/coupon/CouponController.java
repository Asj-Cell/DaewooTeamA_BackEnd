package com.example.backend.coupon;

import com.example.backend.coupon.dto.CouponDto;
import com.example.backend.coupon.CouponService; // CouponService 임포트
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CouponController {

    private final CouponService couponService;
    private final UserRepository userRepository;

    /**
     * 웰컴 쿠폰 발급 API
     */
    @PostMapping("/my/coupons/welcome")
    public ResponseEntity<String> issueWelcomeCoupon(@AuthenticationPrincipal UserDetails userDetails) {
        User user = findUserFromUserDetails(userDetails);
        couponService.WelcomeCoupon(user);

        return ResponseEntity.ok("웰컴 쿠폰이 발급되었습니다.");
    }

    /**
     * 현재 로그인한 사용자의 사용 가능한 쿠폰 목록 조회 API
     */
    @GetMapping("/my/coupons")
    public ResponseEntity<List<CouponDto>> getMyAvailableCoupons(@AuthenticationPrincipal UserDetails userDetails) {
        User user = findUserFromUserDetails(userDetails);
        List<CouponDto> coupons = couponService.getAvailableCouponsForUser(user);
        return ResponseEntity.ok(coupons);
    }

    //코드 재사용
    // UserDetails에서 User 엔티티를 찾아오는 헬퍼 메소드
    private User findUserFromUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            throw new SecurityException("로그인이 필요합니다.");
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }
}