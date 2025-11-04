package com.example.backend.common.exception;

public enum CouponException {

    // 404 Not Found
    COUPON_NOT_FOUND(404700, "해당 쿠폰을 찾을 수 없습니다."),

    // 400 Bad Request (비즈니스 규칙 위반)
    COUPON_ALREADY_USED(400701, "이미 사용된 쿠폰입니다."),
    COUPON_EXPIRED(400702, "만료된 쿠폰입니다."),

    // 403 Forbidden (권한 없음)
    COUPON_NOT_BELONG_TO_USER(403700, "쿠폰 소유자가 일치하지 않습니다."),

    WELCOME_COUPON_ALREADY_ISSUED(400700, "웰컴 쿠폰은 이미 발급되었습니다.");

    private CommonExceptionTemplate t;

    CouponException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
