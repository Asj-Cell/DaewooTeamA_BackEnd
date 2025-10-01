// backend/src/main/java/com/example/backend/review/ReviewService.java
package com.example.backend.review;

import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.review.dto.ReviewPageTotalInfoDto;
import com.example.backend.review.dto.ReviewRequestDto;
import com.example.backend.review.dto.ReviewResponseDto;
import com.example.backend.review.dto.ReviewUpdateDto;
import com.example.backend.review.entity.Review;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;

    public Page<ReviewResponseDto> getReviewsByHotel(Long hotelId, Pageable pageable) {
        //페이징 처리된 리뷰 목록 조회
        Page<Review> reviewPage = reviewRepository.findByHotelIdOrderByIdDesc(hotelId, pageable);
        //Page<Review>를 Page<ReviewResponseDto>로 변환
        Page<ReviewResponseDto> reviewResponseDtoPage = reviewPage.map(review -> new ReviewResponseDto(review));
        // 최종 DTO에 담아 반환
        return reviewResponseDtoPage;
    }

    public ReviewPageTotalInfoDto getReviewTotalCountAndRating(Long hotelId){
        long totalReviews = reviewRepository.countByHotelId(hotelId);   // 1. 특정 호텔의 전체 리뷰 개수 조회
        Double totalRating = reviewRepository.findTotalRatingByHotelId(hotelId);  // 2. 특정 호텔의 평점 합계 조회
        double averageRating = (totalReviews > 0 && totalRating != null) // 3. 평균 평점 계산 (리뷰가 없는 경우 0점)
                ? Math.round((totalRating / totalReviews) * 10.0) / 10.0 // 소수점 첫째 자리까지 반올림
                : 0.0;
        return new ReviewPageTotalInfoDto(averageRating, totalReviews);
    }

    // 리뷰 추가
    @Transactional
    public ReviewResponseDto addReview(Long hotelId, Long userId, ReviewRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 호텔을 찾을 수 없습니다: " + hotelId));

        Review review = new Review();
        review.setUser(user);
        review.setHotel(hotel);
        review.setContent(requestDto.getContent());
        review.setUserRatingScore(requestDto.getUserRatingScore());
        Review savedReview = reviewRepository.save(review);
        return new ReviewResponseDto(savedReview);
    }

    // 리뷰 수정
    @Transactional
    public ReviewResponseDto updateReview(Long reviewId, Long userId, ReviewUpdateDto updateDto) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new IllegalArgumentException("수정 권한이 없거나 리뷰를 찾을 수 없습니다."));
        review.setContent(updateDto.getContent());
        review.setUserRatingScore(updateDto.getUserRatingScore());
        return new ReviewResponseDto(review);
    }

    // 리뷰 삭제
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(() -> new IllegalArgumentException("삭제 권한이 없거나 리뷰를 찾을 수 없습니다."));
        reviewRepository.delete(review);
    }
}