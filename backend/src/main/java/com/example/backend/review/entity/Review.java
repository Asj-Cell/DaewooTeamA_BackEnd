package com.example.backend.review.entity;


import com.example.backend.hotel.entity.Hotel;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "review")
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "content",nullable = false, length = 255)
    private String content;

    @Column(name = "user_rating_score",nullable = false)
    private Double userRatingScore;

    @Column(name = "report_yn" , nullable = false)
    private Boolean reportYn=false; // 신고 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;
}
