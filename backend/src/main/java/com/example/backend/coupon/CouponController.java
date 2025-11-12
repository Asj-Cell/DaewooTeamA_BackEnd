package com.example.backend.coupon;

import com.example.backend.common.exception.MemberException;
import com.example.backend.coupon.dto.CouponDto;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
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
            throw MemberException.LOGIN_REQUIRED.getException();
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        MemberException.USER_NOT_FOUND.getException()
                );
    }
}