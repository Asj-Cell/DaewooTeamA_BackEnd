package com.example.backend.amenities;

import com.example.backend.amenities.entity.Amenities;
import com.example.backend.amenities.dto.AmenitiesDto;
import com.example.backend.common.exception.HotelException;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.HotelService;
import com.example.backend.hotel.entity.Hotel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AmenitiesService {

    private final HotelRepository hotelRepository;
    private final HotelService hotelService;

    public AmenitiesDto updateAmenities(Long hotelId, AmenitiesDto amenitiesDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        HotelException.HOTEL_NOT_FOUND.getException()
                );

        Amenities amenities = hotel.getAmenities();
        if (amenities == null) {
            amenities = new Amenities();
            hotel.setAmenities(amenities);
            amenities.setHotel(hotel);
        }

        hotelService.updateAmenitiesEntity(amenities, amenitiesDto);

        return new AmenitiesDto(
                amenities.isFrontDesk24(), amenities.isOutdoorPool(), amenities.isIndoorPool(),
                amenities.isSpaWellnessCenter(), amenities.isRestaurant(), amenities.isRoomservice(),
                amenities.isFitnessCenter(), amenities.isBarLounge(), amenities.isTeaCoffeeMachine(),
                amenities.isAirConditioner()
        );
    }
}
