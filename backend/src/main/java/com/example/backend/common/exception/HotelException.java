package com.example.backend.common.exception;

public enum HotelException {

    HOTEL_NOT_FOUND(404800, "해당 ID의 호텔을 찾을 수 없습니다."),

    CITY_NOT_FOUND(404801, "해당 ID의 도시를 찾을 수 없습니다.");

    private CommonExceptionTemplate t;

    HotelException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
