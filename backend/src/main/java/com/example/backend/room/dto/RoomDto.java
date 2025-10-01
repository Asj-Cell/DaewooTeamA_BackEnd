package com.example.backend.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private String roomNumber;
    private String name;
    private BigDecimal price;
    private String view;
    private String bed;
    private Integer maxGuests;
    private List<RoomImgDto> roomImages;
    private Boolean isAvailable;
}


