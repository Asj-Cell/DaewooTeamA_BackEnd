package com.example.backend.auth;

import com.example.backend.auth.dto.*;
import com.example.backend.common.util.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth API", description = "인증(회원가입/로그인/로그아웃) 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일과 비밀번호로 회원가입을 진행합니다.")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> signup(@RequestBody SignupRequestDto signupRequestDto) {
        authService.signup(signupRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행하고 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@RequestBody LoginRequestDto loginRequestDto) {
        AuthResponseDto response = authService.login(loginRequestDto);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "비밀번호 찾기 (이메일 발송)", description = "이메일로 비밀번호 재설정을 위한 인증 코드를 발송합니다.")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody ForgotPasswordRequestDto requestDto) {
        authService.forgotPassword(requestDto.getEmail());
        return ResponseEntity.ok(ApiResponse.success("비밀번호 재설정 이메일을 발송했습니다."));
    }

    @Operation(summary = "비밀번호 찾기 (코드 검증)", description = "이메일로 받은 인증 코드를 검증하고 임시 토큰을 발급합니다.")
    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<VerifyCodeResponseDto>> verifyCode(@RequestBody VerifyCodeRequestDto requestDto) {
        VerifyCodeResponseDto response = authService.verifyResetToken(requestDto.getToken());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "비밀번호 재설정", description = "임시 토큰으로 인증 후, 새로운 비밀번호로 재설정합니다.")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ResetPasswordRequestDto requestDto) {

        if (userDetails == null) {
            return ResponseEntity.status(401).body(ApiResponse.fail("인증 정보가 유효하지 않습니다."));
        }

        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new IllegalArgumentException("새 비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Long userId = Long.parseLong(userDetails.getUsername());
        authService.resetPassword(userId, requestDto.getNewPassword());

        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 재설정되었습니다."));
    }
}
