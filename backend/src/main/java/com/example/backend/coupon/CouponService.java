package com.example.backend.coupon;

import com.example.backend.common.exception.CouponException;
import com.example.backend.coupon.CouponRepository;
import com.example.backend.coupon.entity.Coupon;
import com.example.backend.coupon.dto.CouponDto;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {

    private final CouponRepository couponRepository;


    private static final String WELCOME_COUPON_NAME = "신규 가입 5,000원 할인 쿠폰";


    /**
     * 사용자에게 웰컴 쿠폰(5,000원, 2주일)을 발급합니다.
     */
    public void WelcomeCoupon(User user) {
        boolean alreadyHasWelcomeCoupon = couponRepository.existsByUserAndName(user, WELCOME_COUPON_NAME);
        //사용자 재발급 방지
        if (alreadyHasWelcomeCoupon) {
            throw CouponException.WELCOME_COUPON_ALREADY_ISSUED.getException();
        }

        Coupon coupon = new Coupon();
        coupon.setName(WELCOME_COUPON_NAME);
        coupon.setDiscountAmount(new BigDecimal(5000));
        coupon.setExpiryDate(LocalDate.now().plusWeeks(2)); // 2주일 뒤 만료
        coupon.setUser(user);
        coupon.setUsed(false);
        couponRepository.save(coupon);
    }

    /**
     * 특정 사용자가 현재 사용 가능한 쿠폰 목록을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<CouponDto> getAvailableCouponsForUser(User user) {
        LocalDate today = LocalDate.now();
        List<Coupon> availableCoupons = couponRepository.findByUserAndIsUsedFalseAndExpiryDateAfter(user, today);

        return availableCoupons.stream()
                .map(CouponDto::fromEntity)
                .collect(Collectors.toList());
    }
}
