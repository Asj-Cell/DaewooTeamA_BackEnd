package com.example.backend.hotel.hotelfilters.detail;

import com.example.backend.common.exception.HotelException;
import com.example.backend.favorites.FavoritesRepository;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.hotel.entity.HotelImage;
import com.example.backend.hotel.hotelfilters.HotelFiltersService;
import com.example.backend.review.ReviewRepository;
import com.example.backend.room.RoomService;
import com.example.backend.room.dto.RoomDto;
import com.example.backend.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HotelDetailService {

    private final HotelRepository hotelRepository;
    private final RoomService roomService;
    private final ReviewRepository reviewRepository;
    private final FavoritesRepository favoritesRepository;
    private final HotelFiltersService  hotelFiltersService;

    public HotelDetailFiltersDto getHotelDetail(Long hotelId, Long loginUserId, LocalDate checkInDate, LocalDate checkOutDate) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                                HotelException.HOTEL_NOT_FOUND.getException()
                        );

        List<String> amenities = getAmenitiesList(hotel);

        // [수정] 룸 리스트를 가져온 뒤, 각 룸의 예약 가능 여부를 체크하는 로직 추가
        List<RoomDto> rooms = roomService.getRoomsByHotelId(hotelId).stream()
                .peek(roomDto -> {
                    hotel.getRooms().stream()
                            .filter(r -> r.getId().equals(roomDto.getId()))
                            .findFirst()
                            .ifPresent(roomEntity -> roomDto.setIsAvailable(isRoomAvailable(roomEntity, checkInDate, checkOutDate)));
                })
                .collect(Collectors.toList());

        Double totalRating = reviewRepository.findTotalRatingByHotelId(hotelId);
        long reviewCount = reviewRepository.countByHotelId(hotelId);
        double avgRating = (totalRating != null && reviewCount > 0) ? totalRating / reviewCount : 0.0;

        boolean isFavorite = (loginUserId != null) &&
                favoritesRepository.existsByUser_IdAndHotel_Id(loginUserId, hotelId);

        List<String> roomImageUrls = hotel.getRooms().stream()
                .flatMap(room -> room.getImages().stream())
                .map(img -> img.getImageUrl())
                .toList();

        List<String> hotelImageUrls = hotel.getImages().stream()
                .map(HotelImage::getImageUrl)
                .toList();

        return new HotelDetailFiltersDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getGrade(),
                hotelFiltersService.countAmenities(hotel),
                getLowestRoomPrice(hotel),
                avgRating,
                hotelImageUrls,
                isFavorite,
                reviewCount,
                hotel.getCity().getCityName(),
                hotel.getCity().getCountry(),
                amenities,
                rooms,
                hotel.getOverview(),
                roomImageUrls
        );
    }

    private List<String> getAmenitiesList(Hotel h) {
        List<String> list = new ArrayList<>();
        if(h.getAmenities() != null) {
            if (h.getAmenities().isFrontDesk24()) list.add("24시간 프론트");
            if (h.getAmenities().isAirConditioner()) list.add("에어컨");
            if (h.getAmenities().isFitnessCenter()) list.add("헬스장");
            if (h.getAmenities().isOutdoorPool()) list.add("야외 수영장");
            if (h.getAmenities().isIndoorPool()) list.add("실내 수영장");
            if (h.getAmenities().isSpaWellnessCenter()) list.add("스파");
            if (h.getAmenities().isRestaurant()) list.add("레스토랑");
            if (h.getAmenities().isRoomservice()) list.add("룸서비스");
            if (h.getAmenities().isBarLounge()) list.add("바/라운지");
            if (h.getAmenities().isTeaCoffeeMachine()) list.add("차/커피");
        }
        if(h.getFreebies() != null) {
            if (h.getFreebies().isBreakfastIncluded()) list.add("조식 포함");
            if (h.getFreebies().isFreeParking()) list.add("무료 주차");
            if (h.getFreebies().isFreeWifi()) list.add("무료 와이파이");
            if (h.getFreebies().isAirportShuttlebus()) list.add("공항 셔틀버스");
            if (h.getFreebies().isFreeCancellation()) list.add("무료 취소");
        }
        return list;

    }

    private BigDecimal getLowestRoomPrice(Hotel hotel) {
        return hotel.getRooms().stream()
                .map(Room::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    // [추가] 예약 가능 여부를 확인하는 헬퍼 메서드
    private boolean isRoomAvailable(Room room, LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) return true;
        return room.getReservations().stream().noneMatch(reservation ->
                reservation.getCheckinDate().isBefore(checkOut) &&
                        reservation.getCheckoutDate().isAfter(checkIn)
        );
    }
}