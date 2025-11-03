package com.example.backend.common.exception;

public enum CouponException {

    WELCOME_COUPON_ALREADY_ISSUED(400700, "웰컴 쿠폰은 이미 발급되었습니다.");

    private CommonExceptionTemplate t;

    CouponException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
