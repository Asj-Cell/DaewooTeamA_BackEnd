package com.example.backend.common.util;

import lombok.Getter;

@Getter
public class ApiResponse <T>{
    private final boolean success;
    private final T content;
    private final String message;

    // 성공 시 생성자
    private ApiResponse(boolean success, T content, String message) {
        this.success = success;
        this.content = content;
        this.message = message;
    }

    // 실패 시 생성자
    private ApiResponse(boolean success, String message) {
        this.success = success;
        this.content = null;
        this.message = message;
    }

    // 성공 응답을 만드는 static factory method
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "요청에 성공했습니다.");
    }

    // 실패 응답을 만드는 static factory method
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, message);
    }
}
