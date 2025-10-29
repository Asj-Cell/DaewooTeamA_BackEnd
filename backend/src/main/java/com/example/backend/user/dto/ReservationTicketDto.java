package com.example.backend.user.dto;

import com.example.backend.Reservation.Reservation;
import com.example.backend.hotel.entity.HotelImage;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter

public class ReservationTicketDto {

    private final Long reservationId; // 예약을 식별하고 취소할 때 필요한 ID
    // 사용자 정보
    private final String userName;
    private final String userProfileImageUrl;

    // 호텔 및 객실 정보
    private final String hotelName;
    private final String hotelAddress;
    private final String roomName;
    private final String roomBedInfo;
    private final String roomNumber; // 방 번호는 도착 시 배정될 수 있음
    private final String view;
    private final List<String> hotelImageUrl;
    private final String city;
    private final String country;
    private final BigDecimal totalPrice;



    // 예약 날짜 및 시간 정보
    private final LocalDate checkInDate;
    private final LocalDate checkOutDate;
    private final LocalTime checkInTime;
    private final LocalTime checkOutTime;

    // 예약 고유 번호
    private final String bookingReference;

    @Builder
    public ReservationTicketDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.userName = reservation.getUser().getUserName();
        this.userProfileImageUrl = reservation.getUser().getImageUrl();
        this.hotelName = reservation.getRoom().getHotel().getName();
        this.hotelAddress = reservation.getRoom().getHotel().getAddress(); // Hotel 엔티티의 주소 사용
        this.roomName = reservation.getRoom().getName();
        this.roomBedInfo = reservation.getRoom().getBed();
        this.roomNumber = reservation.getRoom().getRoomNumber() != null ? reservation.getRoom().getRoomNumber() : "On arrival";
        this.view = reservation.getRoom().getView();
        this.hotelImageUrl = reservation.getRoom().getHotel().getImages().stream()
                .map(HotelImage::getImageUrl)
                .collect(Collectors.toList());
        this.city = reservation.getRoom().getHotel().getCity().getCityName();
        this.country = reservation.getRoom().getHotel().getCity().getCountry();
        this.totalPrice = reservation.getTotalPrice();
        this.checkInDate = reservation.getCheckinDate();
        this.checkOutDate = reservation.getCheckoutDate();
        this.checkInTime = reservation.getRoom().getHotel().getCheckinTime(); // Hotel 엔티티의 체크인 시간 사용
        this.checkOutTime = reservation.getRoom().getHotel().getCheckoutTime(); // Hotel 엔티티의 체크아웃 시간 사용
        this.bookingReference = "EK-" + reservation.getId(); // 예약 ID를 기반으로 고유 번호 생성
    }
}
