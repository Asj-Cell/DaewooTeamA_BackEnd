package com.example.backend.common.exception;

public enum PayException {
    AMOUNT_DISCREPANCY(400100,"요청된 결제 금액이 서버에서 계산된 금액과 일치하지 않습니다."),

    PAYMENT_INFORMATION_NOT_FOUND(404900,"결제 정보를 찾을 수 없습니다."),

    TOSS_CONFIRM_FAILED(500900, "토스페이먼츠 결제 승인 API 호출에 실패했습니다."),

    TOSS_CANCEL_FAILED(500901, "토스페이먼츠 결제 취소 API 호출에 실패했습니다.");

    private CommonExceptionTemplate t;

    PayException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
