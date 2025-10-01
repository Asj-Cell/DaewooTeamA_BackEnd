package com.example.backend.user;

import com.example.backend.common.util.ApiResponse;
import com.example.backend.user.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User API", description = "✅ 인증 필요 | 사용자 정보 관련 API")
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 비밀번호 변경", description = "로그인한 사용자의 비밀번호를 변경합니다.")
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ChangePasswordRequestDto requestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        userService.changeUserPassword(userId, requestDto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }
    
    @Operation(summary = "내 프로필 이미지 변경", description = "로그인한 사용자의 프로필 이미지를 변경합니다.")
    @PutMapping("/me/profile-image")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateUserProfileImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("image") MultipartFile imageFile) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String newImageUrl = userService.updateUserProfileImage(userId, imageFile);
        return ResponseEntity.ok(ApiResponse.success(Map.of("imageUrl", newImageUrl)));
    }

    @Operation(summary = "내 배경 이미지 변경", description = "로그인한 사용자의 배경 이미지를 변경합니다.")
    @PutMapping("/me/background-image")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateUserBackgroundImage(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("image") MultipartFile imageFile) {
        Long userId = Long.parseLong(userDetails.getUsername());
        String newBackGroundImageUrl = userService.updateUserBackgroundImage(userId, imageFile);
        return ResponseEntity.ok(ApiResponse.success(Map.of("backGroundImageUrl", newBackGroundImageUrl)));
    }

    @Operation(summary = "내 프로필 페이지 조회", description = "로그인한 사용자의 프로필 페이지 정보를 조회합니다.")
    @GetMapping("/me/profileAll")
    public ResponseEntity<ApiResponse<UserProfileAllResponseDto>> getUserProfileAll(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        UserProfileAllResponseDto userProfile = userService.getUserProfileAll(userId);
        return ResponseEntity.ok(ApiResponse.success(userProfile));
    }

    @Operation(summary = "내 프로필 정보 조회", description = "로그인한 사용자의 프로필 정보를 조회합니다.")
    @GetMapping("/me/profile")
    public ApiResponse<UserProfileResponseDto> getUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        UserProfileResponseDto profileDto = userService.getUserProfile(userId);
        return ApiResponse.success(profileDto);
    }

    @Operation(summary = "내 예약 내역 조회", description = "로그인한 사용자의 예약 내역을 조회합니다.")
    @GetMapping("/me/reservations")
    public ApiResponse<List<ReservationDetailDto>> getUserReservations(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<ReservationDetailDto> result = userService.getUserReservations(userId);
        return ApiResponse.success(result);
    }

    @Operation(summary = "내 결제 수단 조회", description = "로그인한 사용자의 결제 수단 목록을 조회합니다.")
    @GetMapping("/me/payments")
    public ApiResponse<List<UserProfilePaymentMethodDto>> getMyPaymentMethods(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<UserProfilePaymentMethodDto> paymentMethods = userService.getUserPaymentMethods(userId);
        return ApiResponse.success(paymentMethods);
    }

    @Operation(summary = "내 프로필 정보 수정", description = "로그인한 사용자의 프로필 정보를 수정합니다.")
    @PutMapping("/me/profile-info")
    public ResponseEntity<ApiResponse<UserProfileAllResponseDto>> updateUserProfileInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserProfileRequestDto requestDto) {
        Long userId = Long.parseLong(userDetails.getUsername());
        UserProfileAllResponseDto updatedProfile = userService.updateUserProfileInfo(userId, requestDto);
        return ResponseEntity.ok(ApiResponse.success(updatedProfile));
    }
}