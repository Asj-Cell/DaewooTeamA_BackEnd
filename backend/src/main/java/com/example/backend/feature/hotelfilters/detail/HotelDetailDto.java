package com.example.backend.feature.hotelfilters.detail;

import com.example.backend.feature.hotelfilters.dto.HotelDto;
import com.example.backend.room.dto.RoomDto;
import com.example.backend.room.dto.RoomImgDto;
import com.example.backend.room.entity.RoomImg;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDetailDto extends HotelDto {

    // 호텔의 상세 편의시설 (문자열 목록으로 내려주기)
    private List<String> amenities;

    // 호텔에 속한 방 목록
    private List<RoomDto> rooms;

    // 호텔 소개/설명
    private String overview;

    // 호텔 단위에서 대표로 보여줄 URL들
    private List<String> roomImageUrls;


    // 생성자
    public HotelDetailDto(Long id,
                          String name,
                          String address,
                          Integer grade,
                          int amenitiesCount,
                          BigDecimal price,
                          double rating,
                          List<String> imageUrls,
                          boolean isFavorite,
                          Long reviewCount,
                          List<String> amenities,
                          List<RoomDto> rooms,
                          String overview,
                          List<String> roomImageUrls) {
        super(id, name, address, grade, amenitiesCount, price, rating, imageUrls, isFavorite, reviewCount);
        this.amenities = amenities;
        this.rooms = rooms;
        this.overview = overview;
        this.roomImageUrls = roomImageUrls;
    }
}
