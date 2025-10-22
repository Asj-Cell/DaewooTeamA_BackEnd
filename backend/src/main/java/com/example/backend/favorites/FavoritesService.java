package com.example.backend.favorites;

import com.example.backend.hotel.hotelfilters.dto.HotelFiltersDto;
import com.example.backend.favorites.entity.Favorites;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.hotel.entity.HotelImage;
import com.example.backend.room.entity.Room;
import com.example.backend.review.ReviewRepository;
import com.example.backend.user.UserRepository;
import com.example.backend.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;


    public List<HotelFiltersDto> getFavoriteHotels(Long userId) {
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

                    return new HotelFiltersDto(
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
    @Transactional // 데이터를 변경하는 작업이므로 readOnly가 아닌 Transactional 추가
    public boolean toggleFavorite(Long userId, Long hotelId) {
        // 1. 찜 목록에 이미 존재하는지 확인
        Optional<Favorites> favoriteOpt = favoritesRepository.findByUser_IdAndHotel_Id(userId, hotelId);

        if (favoriteOpt.isPresent()) {
            // 2. 존재하면, 찜 목록에서 삭제
            favoritesRepository.delete(favoriteOpt.get());
            return false; // 찜 삭제됨
        } else {
            // 3. 존재하지 않으면, 찜 목록에 추가
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다. ID: " + userId));
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("해_당 호텔을 찾을 수 없습니다. ID: " + hotelId));

            Favorites newFavorite = new Favorites();
            newFavorite.setUser(user);
            newFavorite.setHotel(hotel);

            favoritesRepository.save(newFavorite);
            return true; // 찜 추가됨
        }
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
