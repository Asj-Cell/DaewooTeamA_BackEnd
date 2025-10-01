package com.example.backend.review;

import com.example.backend.common.util.ApiResponse;
import com.example.backend.review.dto.ReviewPageTotalInfoDto;
import com.example.backend.review.dto.ReviewRequestDto;
import com.example.backend.review.dto.ReviewResponseDto;
import com.example.backend.review.dto.ReviewUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels/{hotelId}/reviews")
@RequiredArgsConstructor
@Tag(name = "Review API", description = "✅ 인증 필요 (조회 제외) | 리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    // ... (리뷰 조회 GET 메서드들은 변경 없음)
    @Operation(summary = "호텔별 리뷰 조회", description = "특정 호텔에 대한 리뷰를 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReviewResponseDto>>> getReviewsByHotel(@PathVariable Long hotelId, Pageable pageable) {
        Page<ReviewResponseDto> reviewsByHotel = reviewService.getReviewsByHotel(hotelId, pageable);
        return ResponseEntity.ok(ApiResponse.success(reviewsByHotel));
    }

    @Operation(summary = "호텔별 리뷰 평균 점수 및 개수 ", description = "특정 호텔에 대한 total 정보를 조회합니다.")
    @GetMapping("/total-info")
    public ResponseEntity<ApiResponse<ReviewPageTotalInfoDto>> getReviewsTotalInfoByHotel(@PathVariable Long hotelId) {
        ReviewPageTotalInfoDto reviewTotalCountAndRating = reviewService.getReviewTotalCountAndRating(hotelId);
        return ResponseEntity.ok(ApiResponse.success(reviewTotalCountAndRating));
    }


    @Operation(summary = "호텔 리뷰 작성", description = "특정 호텔에 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponseDto>> addReview(
            @PathVariable Long hotelId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReviewRequestDto requestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ReviewResponseDto newReview = reviewService.addReview(hotelId, userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(newReview));
    }

    @Operation(summary = "호텔 리뷰 수정", description = "리뷰를 수정합니다. (본인만 가능)")
    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReviewUpdateDto updateDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ReviewResponseDto updatedReview = reviewService.updateReview(reviewId, userId, updateDto);
        return ResponseEntity.ok(ApiResponse.success(updatedReview));
    }

    @Operation(summary = "호텔 리뷰 삭제", description = "리뷰를 삭제합니다. (본인만 가능)")
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.ok(ApiResponse.success("리뷰(ID: " + reviewId + ")가 성공적으로 삭제되었습니다."));
    }
}