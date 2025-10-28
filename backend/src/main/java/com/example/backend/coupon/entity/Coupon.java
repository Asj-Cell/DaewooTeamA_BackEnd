package com.example.backend.coupon.entity;

import com.example.backend.Reservation.Reservation;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Table(name = "coupon")
@Getter @Setter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name; // 예: "신규 가입 5,000원 할인 쿠폰"

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount; // 예: 5000.00

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate; // 만료일 (이 날짜 전까지 사용 가능)

    @Column(name = "is_used", nullable = false)
    private boolean isUsed = false; // 사용 여부 (true = 사용됨)

    // 이 쿠폰의 소유자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 이 쿠폰이 사용된 예약 (사용 전에는 null)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true)
    private Reservation reservation;
}
