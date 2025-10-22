package com.example.backend.coupon;

import com.example.backend.coupon.entity.Coupon;
import com.example.backend.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    /**
     * 사용자가 사용 가능한(사용 안 함, 만료되지 않음) 쿠폰 목록을 조회합니다.
     * @param user 쿠폰 소유자
     * @param today 현재 날짜
     * @return 사용 가능한 쿠폰 리스트
     */
    List<Coupon> findByUserAndIsUsedFalseAndExpiryDateAfter(User user, LocalDate today);
}
