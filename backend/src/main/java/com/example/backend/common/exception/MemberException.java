package com.example.backend.common.exception;

public enum MemberException {

    NOT_EXIST_MEMBER_ID(400400, "그런 아이디 없음"),

    USER_NOT_FOUND(404400, "해당 ID의 사용자를 찾을 수 없습니다."),

    LOGIN_REQUIRED(401401, "로그인이 필요합니다. (토큰 없음)");

    private CommonExceptionTemplate t;

    MemberException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
