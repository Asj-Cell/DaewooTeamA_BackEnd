package com.example.backend.feature.hotelfilters;

import com.example.backend.favorites.FavoritesRepository;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.feature.hotelfilters.dto.HotelDto;
import com.example.backend.feature.hotelfilters.dto.HotelFilterRequestDto;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.entity.HotelImage;
import com.example.backend.room.entity.Room;
import com.example.backend.review.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelFiltersService {

    private final HotelRepository hotelRepository;
    private final ReviewRepository reviewRepository;
    private final FavoritesRepository favoritesRepository;

    public Page<HotelDto> filterHotels(HotelFilterRequestDto request, Pageable pageable, Long loginUserId) {
        // 1. Specification으로 DB에서 필터 적용, 전체 조회
        Specification<Hotel> spec = HotelSpecifications.withFilters(request);
        List<Hotel> hotels = hotelRepository.findAll(spec);

        // 2. DTO 변환 + Stream 필터 + 정렬
        List<HotelDto> sortedDtos = hotels.stream()
                .map(h -> {
                    // 리뷰 기반 평점 계산
                    Double totalRating = reviewRepository.findTotalRatingByHotelId(h.getId());
                    long reviewCount = reviewRepository.countByHotelId(h.getId());
                    double avgRating = (totalRating != null && reviewCount > 0) ? totalRating / reviewCount : 0.0;


                    boolean isFavorite = (loginUserId != null) && favoritesRepository.existsByUser_IdAndHotel_Id(loginUserId, h.getId());

                    List<String> hotelImageUrls = h.getImages().stream()
                            .map(HotelImage::getImageUrl)
                            .toList();

                    return new HotelDto(
                            h.getId(),
                            h.getName(),
                            h.getAddress(),
                            h.getGrade(),
                            countAmenities(h),
                            getLowestAvailablePrice(h, request),
                            avgRating,
                            hotelImageUrls,
                            isFavorite,
                            reviewCount
                    );
                })
                .filter(dto -> request.getMinAvgRating() == null || dto.getRating() >= request.getMinAvgRating())
                .sorted(getComparator(request.getSortBy()))
                .collect(Collectors.toList());

        // 3. 정렬 후 메모리 페이징
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sortedDtos.size());
        List<HotelDto> pageContent = sortedDtos.subList(start, end);

        return new PageImpl<>(pageContent, pageable, sortedDtos.size());
    }

    // 정렬 기준 Comparator 반환
    private Comparator<HotelDto> getComparator(String sortBy) {
        if ("rating".equalsIgnoreCase(sortBy)) {
            return Comparator.comparingDouble(HotelDto::getRating).reversed(); // 평점 내림차순
        } else if ("priceAsc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(HotelDto::getPrice); // 가격 오름차순
        } else if ("priceDesc".equalsIgnoreCase(sortBy)) {
            return Comparator.comparing(HotelDto::getPrice).reversed(); // 가격 내림차순
        } else {
            return Comparator.comparingLong(HotelDto::getId); // 기본: ID 순
        }
    }

    // 호텔이 가지고 있는 무료서비스 + 편의시설 카운트
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

    private boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) return true;
        return room.getReservations().stream().noneMatch(reservation ->
                reservation.getCheckinDate().isBefore(checkOut) &&
                        reservation.getCheckoutDate().isAfter(checkIn)
        );
    }

    private BigDecimal getLowestAvailablePrice(Hotel h, HotelFilterRequestDto request) {
        return h.getRooms().stream()
                .filter(r -> isRoomAvailable(r, request.getCheckInDate(), request.getCheckOutDate()))
                .filter(r -> request.getMinAvailableRooms() == null || r.getMaxGuests() >= request.getMinAvailableRooms())
                .map(Room::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }
}
