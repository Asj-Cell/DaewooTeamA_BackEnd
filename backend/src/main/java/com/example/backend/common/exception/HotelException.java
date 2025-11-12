package com.example.backend.common.exception;

public enum HotelException {

    HOTEL_NOT_FOUND(404800, "해당 ID의 호텔을 찾을 수 없습니다."),

    CITY_NOT_FOUND(404801, "해당 ID의 도시를 찾을 수 없습니다."),

    FREEBIES_NOT_FOUND(404802,"해당 id의 무료서비스를 찾을 수 없습니다."),

    AMENITIES_NOT_FOUND(404803,"해당 id의 편의시설을 찾을 수 없습니다."),

    ROOM_NOT_FOUND(404804,"해당 id의 방을 찾을 수 없습니다."),

    ROOM_ALREADY_BOOKED(404805,"해당 날짜에 이미 예약된 방입니다.");

    private CommonExceptionTemplate t;

    HotelException(int code, String message) {
        this.t = new CommonExceptionTemplate(code, message);
    }

    public CommonExceptionTemplate getException() {
        return this.t;
    }
}
