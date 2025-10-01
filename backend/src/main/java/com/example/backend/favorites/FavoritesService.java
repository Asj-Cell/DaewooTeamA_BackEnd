package com.example.backend.favorites;

import com.example.backend.feature.hotelfilters.dto.HotelDto;
import com.example.backend.favorites.entity.Favorites;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.hotel.entity.HotelImage;
import com.example.backend.room.entity.Room;
import com.example.backend.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final ReviewRepository reviewRepository;


    public List<HotelDto> getFavoriteHotels(Long userId) {
        List<Favorites> favorites = favoritesRepository.findAllByUser_Id(userId);

        return favorites.stream()
                .map(fav -> {
                    Hotel h = fav.getHotel();

                    Double totalRating = reviewRepository.findTotalRatingByHotelId(h.getId());
                    long reviewCount = reviewRepository.countByHotelId(h.getId());
                    double avgRating = (totalRating != null && reviewCount > 0) ? totalRating / reviewCount : 0.0;

                    List<String> hotelImageUrls = h.getImages().stream()
                            .map(HotelImage::getImageUrl)
                            .toList();

                    return new HotelDto(
                            h.getId(),
                            h.getName(),
                            h.getAddress(),
                            h.getGrade(),
                            countAmenities(h),
                            getLowestAvailablePrice(h),
                            avgRating,
                            hotelImageUrls,
                            true, // 찜 여부 무조건 true
                            reviewCount
                    );
                })
                .collect(Collectors.toList());
    }

    // 호텔이 가지고 있는 무료 서비스 + 편의시설 카운트
    private int countAmenities(Hotel h) {
        int count = 0;
        // Freebies
        if (h.getFreebies().isBreakfastIncluded()) count++;
        if (h.getFreebies().isFreeParking()) count++;
        if (h.getFreebies().isFreeWifi()) count++;
        if (h.getFreebies().isAirportShuttlebus()) count++;
        if (h.getFreebies().isFreeCancellation()) count++;

        // Amenities
        if (h.getAmenities().isFrontDesk24()) count++;
        if (h.getAmenities().isAirConditioner()) count++;
        if (h.getAmenities().isFitnessCenter()) count++;
        if (h.getAmenities().isOutdoorPool() || h.getAmenities().isIndoorPool()) count++; // 수영장 합치기
        if (h.getAmenities().isSpaWellnessCenter()) count++;
        if (h.getAmenities().isRestaurant()) count++;
        if (h.getAmenities().isRoomservice()) count++;
        if (h.getAmenities().isBarLounge()) count++;
        if (h.getAmenities().isTeaCoffeeMachine()) count++;

        return count;
    }

    // 가격 최저값 계산
    private BigDecimal getLowestAvailablePrice(Hotel h) {
        return h.getRooms().stream()
                .map(Room::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

}
