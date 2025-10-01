package com.example.backend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 인증 코드 검증 성공 시, 임시 토큰을 반환하기 위한 DTO
 */
@Getter
@AllArgsConstructor
public class VerifyCodeResponseDto {
    private String temporaryToken;
}
