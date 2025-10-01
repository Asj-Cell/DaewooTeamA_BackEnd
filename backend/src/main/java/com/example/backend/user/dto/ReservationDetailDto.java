package com.example.backend.user.dto;
import com.example.backend.Reservation.Reservation;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
public class ReservationDetailDto {

    private String hotelName;
    private String roomNumber;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private LocalTime hotelCheckinTime;
    private LocalTime hotelCheckoutTime;
    private String hotelImageUrl;

    public ReservationDetailDto(Reservation reservation) {
        this.hotelName = reservation.getRoom().getHotel().getName();
        this.roomNumber = reservation.getRoom().getRoomNumber();
        this.checkinDate = reservation.getCheckinDate();
        this.checkoutDate = reservation.getCheckoutDate();
        this.hotelCheckinTime = reservation.getRoom().getHotel().getCheckinTime();
        this.hotelCheckoutTime = reservation.getRoom().getHotel().getCheckoutTime();

        if (reservation.getRoom().getHotel().getImages() != null && !reservation.getRoom().getHotel().getImages().isEmpty()) {
            this.hotelImageUrl = reservation.getRoom().getHotel().getImages().get(0).getImageUrl();
        } else {
            this.hotelImageUrl = null;
        }
    }
}