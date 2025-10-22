package com.example.backend.amenities;

import com.example.backend.amenities.entity.Amenities;
import com.example.backend.amenities.dto.AmenitiesDto;
import com.example.backend.hotel.HotelRepository;
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

    public AmenitiesDto updateAmenities(Long hotelId, AmenitiesDto amenitiesDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + hotelId));

        Amenities amenities = hotel.getAmenities();
        if (amenities == null) {
            amenities = new Amenities();
            hotel.setAmenities(amenities);
        }

        // HotelService의 헬퍼 메소드를 재사용하거나 여기에 직접 구현
        amenities.setFrontDesk24(amenitiesDto.isFrontDesk24());
        amenities.setOutdoorPool(amenitiesDto.isOutdoorPool());
        amenities.setIndoorPool(amenitiesDto.isIndoorPool());
        amenities.setSpaWellnessCenter(amenitiesDto.isSpaWellnessCenter());
        amenities.setRestaurant(amenitiesDto.isRestaurant());
        amenities.setRoomservice(amenitiesDto.isRoomservice());
        amenities.setFitnessCenter(amenitiesDto.isFitnessCenter());
        amenities.setBarLounge(amenitiesDto.isBarLounge());
        amenities.setTeaCoffeeMachine(amenitiesDto.isTeaCoffeeMachine());
        amenities.setAirConditioner(amenitiesDto.isAirConditioner());

        return new AmenitiesDto(
                amenities.isFrontDesk24(), amenities.isOutdoorPool(), amenities.isIndoorPool(),
                amenities.isSpaWellnessCenter(), amenities.isRestaurant(), amenities.isRoomservice(),
                amenities.isFitnessCenter(), amenities.isBarLounge(), amenities.isTeaCoffeeMachine(),
                amenities.isAirConditioner()
        );
    }
}
