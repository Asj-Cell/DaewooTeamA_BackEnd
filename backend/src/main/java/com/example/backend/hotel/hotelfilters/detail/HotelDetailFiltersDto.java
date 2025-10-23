package com.example.backend.hotel.hotelfilters.detail;

import com.example.backend.hotel.hotelfilters.dto.HotelFiltersDto;
import com.example.backend.room.dto.RoomDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDetailFiltersDto extends HotelFiltersDto {

    // 호텔의 상세 편의시설 (문자열 목록으로 내려주기)
    private List<String> amenities;

    // 호텔에 속한 방 목록
    private List<RoomDto> rooms;

    // 호텔 소개/설명
    private String overview;

    // 호텔 단위에서 대표로 보여줄 URL들
    private List<String> roomImageUrls;


    // 생성자
    public HotelDetailFiltersDto(Long id,
                                 String name,
                                 String address,
                                 Integer grade,
                                 int amenitiesCount,
                                 BigDecimal price,
                                 double rating,
                                 List<String> imageUrls,
                                 boolean isFavorite,
                                 Long reviewCount,
                                 String cityName,
                                 List<String> amenities,
                                 List<RoomDto> rooms,
                                 String overview,
                                 List<String> roomImageUrls) {
        super(id, name, address, grade, amenitiesCount, price,rating, imageUrls, isFavorite, reviewCount, cityName);
        this.amenities = amenities;
        this.rooms = rooms;
        this.overview = overview;
        this.roomImageUrls = roomImageUrls;
    }
}
