package com.example.backend.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 인증 코드(토큰) 확인 요청을 위한 DTO
 */
@Getter
@NoArgsConstructor
public class VerifyCodeRequestDto {
    private String token;
}
