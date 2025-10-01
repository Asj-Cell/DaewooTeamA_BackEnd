package com.example.backend.payment.entity;

import com.example.backend.pay.Pay;
import com.example.backend.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter
@Setter
@Table(name = "payment")
@NoArgsConstructor
@SQLDelete(sql = "UPDATE payment SET is_deleted = true WHERE id = ?")
@Where(clause = "is_deleted = false") // 애는 고객의 카드 정보만 있음
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_name",  length = 100)
    private String paymentName;

    @Column(name = "payment_number", length = 50)
    private String paymentNumber;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "cvc", length = 10)
    private String cvc;

    @Column(name = "card_user", length = 100)
    private String cardUser;

    @Column(name = "country", length = 100)
    private String country; //지역/도시

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "payment")
    private List<Pay> pays = new ArrayList<>();

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public void setExpirationDate(String monthYear) {
        // "MM/yy" 형식의 포맷터를 준비합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");

        // "02/27" 문자열을 YearMonth 객체로 파싱합니다.
        YearMonth ym = YearMonth.parse(monthYear, formatter);

        // 해당 월의 마지막 날짜로 LocalDate 객체를 만들어 저장합니다.
        this.expirationDate = ym.atEndOfMonth();
    }
}
