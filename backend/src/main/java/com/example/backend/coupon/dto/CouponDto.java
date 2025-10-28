package com.example.backend.coupon.dto;

import com.example.backend.coupon.entity.Coupon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {
    private Long id;
    private String name;
    private BigDecimal discountAmount;
    private LocalDate expiryDate;

    public static CouponDto fromEntity(Coupon coupon) {
        return new CouponDto(
                coupon.getId(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                coupon.getExpiryDate()
        );
    }
}
