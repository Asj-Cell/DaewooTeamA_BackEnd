package com.example.backend.Reservation;

import com.example.backend.pay.dto.PaymentPageDto;
import com.example.backend.review.ReviewService;
import com.example.backend.review.dto.ReviewPageTotalInfoDto;
import com.example.backend.room.RoomRepository;
import com.example.backend.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final RoomRepository roomRepository;
    private final ReviewService reviewService; // 기존에 만들어둔 ReviewService 활용

    @Transactional(readOnly = true)
    public PaymentPageDto getPaymentPreviewDetails(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new RuntimeException("Room not found"));
        ReviewPageTotalInfoDto reviewInfo = reviewService.getReviewTotalCountAndRating(room.getHotel().getId());
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        BigDecimal totalPrice = room.getPrice().multiply(new BigDecimal(nights));

        return PaymentPageDto.builder()
                .hotelName(room.getHotel().getName())
                .roomName(room.getName())
                .checkInDate(checkInDate)
                .checkoutDate(checkOutDate)
                .nights(nights)
                .totalPrice(totalPrice)
                .reviewCount(reviewInfo.getTotalReviews())
                .avgRating(reviewInfo.getAverageRating())
                .build();
    }
}
