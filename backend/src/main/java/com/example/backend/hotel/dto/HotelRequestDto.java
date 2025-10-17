package com.example.backend.hotel.dto;

import com.example.backend.amenities.dto.AmenitiesDto;
import com.example.backend.freebies.dto.FreebiesDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelRequestDto {
    private String name;
    private Integer grade;
    private String overview;
    private double latitude;
    private double longitude;
    private String address;
    private LocalTime checkinTime;
    private LocalTime checkoutTime;

    private Long cityId;

    private AmenitiesDto amenities;
    private FreebiesDto freebies;
}
