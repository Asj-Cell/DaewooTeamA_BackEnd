package com.example.backend.common.exception;

public enum MemberException {

    USER_NOT_FOUND(404400, "해당 사용자를 찾을 수 없습니다."),

    LOGIN_REQUIRED(401401, "로그인이 필요합니다. (토큰 없음)"),

    NOT_EQUAL_NEW_PW(400600,"새 비밀번호가 일치하지 않습니다."),

    NOT_EQUAL_PW(400600,"비밀번호가 일치하지 않습니다."),

    DONT_CHANGE_PW(400601,"소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다.");

    private CommonExceptionTemplate t;

    MemberException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
