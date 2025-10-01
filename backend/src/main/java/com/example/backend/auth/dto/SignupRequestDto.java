package com.example.backend.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupRequestDto {
    private String userName; // 회원가입 UI에는 First/Last Name이 있지만, User 엔티티의 userName 필드에 합쳐서 저장하겠습니다.
    private String email;
    private String password;
    private String phoneNumber;
}