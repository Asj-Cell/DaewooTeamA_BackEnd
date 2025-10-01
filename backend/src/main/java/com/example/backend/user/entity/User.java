package com.example.backend.user.entity;

import com.example.backend.Reservation.Reservation;
import com.example.backend.review.entity.Review;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name",nullable = false, length = 100)
    private String userName;

    @Column(name = "email",nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "password", length = 100)
    private String password;

    @Column(name = "provider")
    private String provider; // 예: "google", "facebook"

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @Column(name = "address",length = 100)
    private String address;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "image_url",length = 255)
    private String imageUrl;

    @Column(name = "background_image_url",length = 255)
    private String backGroundImageUrl;

    // @Column(name = "verification_code")
    // private String verificationCode; // <-- 이 부분을 주석 처리하거나 삭제합니다.

    @Column(name = "enabled")
    private boolean enabled = false; // 계정 활성화 상태

    @Column(name = "reset_token")
    private String resetToken; // 비밀번호 재설정 토큰

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry; // 재설정 토큰 만료 시간

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();
}
