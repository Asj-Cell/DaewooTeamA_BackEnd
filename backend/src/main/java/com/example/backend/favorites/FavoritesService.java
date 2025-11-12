package com.example.backend.favorites;

import com.example.backend.common.exception.HotelException;
import com.example.backend.common.exception.MemberException;
import com.example.backend.hotel.HotelService;
import com.example.backend.hotel.hotelfilters.HotelFiltersService;
import com.example.backend.hotel.hotelfilters.detail.HotelDetailService;
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
    private final HotelDetailService hotelDetailService;


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
                            hotelDetailService.countAmenities(h),
                            hotelDetailService.getLowestAvailablePrice(h),
                            avgRating,
                            hotelImageUrls,
                            true, // 찜 여부 무조건 true
                            reviewCount,
                            h.getCity().getCityName(),
                            h.getCity().getCountry()
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
                    .orElseThrow(() ->
                            MemberException.USER_NOT_FOUND.getException()
                    );
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() ->
                            HotelException.HOTEL_NOT_FOUND.getException()
                    );

            Favorites newFavorite = new Favorites();
            newFavorite.setUser(user);
            newFavorite.setHotel(hotel);

            favoritesRepository.save(newFavorite);
            return true; // 찜 추가됨
        }
    }


}
