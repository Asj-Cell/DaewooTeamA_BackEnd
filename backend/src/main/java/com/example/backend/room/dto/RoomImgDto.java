package com.example.backend.room.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoomImgDto {
    private Long id;
    private String roomImgUrl;
    private Integer size;
}

