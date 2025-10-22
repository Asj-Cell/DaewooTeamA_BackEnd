package com.example.backend.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 객실(Room)을 생성하고 수정할 때 '요청'에 사용하는 DTO 입니다.
 * ID는 자동으로 생성되므로 포함하지 않습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDto {
    private String roomNumber;
    private BigDecimal price;
    private String name;
    private String view;
    private String bed;
    private Integer maxGuests;

}
