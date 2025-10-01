package com.example.backend.common.exception;

public enum MemberException {

    NOT_EXIST_MEMBER_ID(400400, "그런 아이디 없음");

    private CommonExceptionTemplate t;

    MemberException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
