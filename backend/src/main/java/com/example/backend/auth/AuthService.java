package com.example.backend.auth;

import com.example.backend.auth.dto.AuthResponseDto;
import com.example.backend.auth.dto.LoginRequestDto;
import com.example.backend.auth.dto.SignupRequestDto;
import com.example.backend.auth.dto.VerifyCodeResponseDto; // DTO import 추가
import com.example.backend.common.util.EmailService;
import com.example.backend.common.util.JwtUtil;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    @Transactional
    public void signup(SignupRequestDto signupRequestDto) {
        if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }

        User user = new User();
        user.setUserName(signupRequestDto.getUserName());
        user.setEmail(signupRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
        user.setPhoneNumber(signupRequestDto.getPhoneNumber());

        userRepository.save(user);
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByEmail(loginRequestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.generateToken(user.getId());
        return new AuthResponseDto(token);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자를 찾을 수 없습니다."));

        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        String token = String.valueOf(code);

        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1)); // 토큰 유효시간: 1시간
        userRepository.save(user);

        String emailBody = "비밀번호 재설정을 위한 인증 코드입니다: " + token;

        emailService.sendEmail(user.getEmail(),
                "비밀번호 재설정 인증 코드",
                emailBody);
    }

    // ▼▼▼▼▼ 2단계: 인증 코드 검증 후 임시 토큰 발급 ▼▼▼▼▼
    @Transactional(readOnly = true)
    public VerifyCodeResponseDto verifyResetToken(String token) {
        User user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 코드입니다."));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("인증 코드가 만료되었습니다.");
        }
        // 인증 성공 시, 임시 토큰 발급
        String temporaryToken = jwtUtil.generateTemporaryToken(user.getId());
        return new VerifyCodeResponseDto(temporaryToken);
    }
    // ▲▲▲▲▲ 여기까지 수정 ▲▲▲▲▲

    // ▼▼▼▼▼ 3단계: 임시 토큰으로 사용자 인증 후 비밀번호 변경 ▼▼▼▼▼
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 새로운 비밀번호 암호화 및 저장
        user.setPassword(passwordEncoder.encode(newPassword));

        // 비밀번호 재설정 완료 후, 재설정 토큰 정보 초기화
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }
    // ▲▲▲▲▲ 여기까지 수정 ▲▲▲▲▲
}