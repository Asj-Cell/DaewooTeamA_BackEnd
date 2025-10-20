package com.example.backend.hotel;

import com.example.backend.amenities.dto.AmenitiesDto;
import com.example.backend.amenities.entity.Amenities;

import com.example.backend.freebies.dto.FreebiesDto;
import com.example.backend.freebies.entity.Freebies;
import com.example.backend.hotel.dto.HotelDto;
import com.example.backend.hotel.dto.HotelRequestDto;
import com.example.backend.hotel.entity.City;
import com.example.backend.hotel.entity.Hotel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelService {

    private final HotelRepository hotelRepository;
    private final CityRepository cityRepository; // CityRepository 주입 필요

    public HotelDto createHotel(HotelRequestDto request) {
        City city = cityRepository.findById(request.getCityId())
                .orElseThrow(() -> new EntityNotFoundException("City not found with id: " + request.getCityId()));

        Hotel hotel = new Hotel();
        updateHotelEntityFromRequest(hotel, request);
        hotel.setCity(city);

        // 자식 엔티티(Amenities, Freebies)는 부모인 Hotel에 연관관계를 설정해주어야 함
        if (request.getAmenities() != null) {
            Amenities amenities = new Amenities();
            updateAmenitiesEntity(amenities, request.getAmenities());
            hotel.setAmenities(amenities); // 연관관계 설정
            amenities.setHotel(hotel);
        }

        if (request.getFreebies() != null) {
            Freebies freebies = new Freebies();
            updateFreebiesEntity(freebies, request.getFreebies());
            hotel.setFreebies(freebies); // 연관관계 설정
            freebies.setHotel(hotel);
        }

        Hotel savedHotel = hotelRepository.save(hotel);
        return convertToHotelDto(savedHotel);
    }

    public HotelDto updateHotel(Long hotelId, HotelRequestDto request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + hotelId));

        updateHotelEntityFromRequest(hotel, request);

        // 연관된 City 정보도 업데이트가 필요하다면 로직 추가
        if (!hotel.getCity().getId().equals(request.getCityId())) {
            City city = cityRepository.findById(request.getCityId())
                    .orElseThrow(() -> new EntityNotFoundException("City not found with id: ".concat(request.getCityId().toString())));
            hotel.setCity(city);
        }

        if (request.getAmenities() != null) {
            Amenities amenities = (hotel.getAmenities() == null) ? new Amenities() : hotel.getAmenities();
            updateAmenitiesEntity(amenities, request.getAmenities());
            hotel.setAmenities(amenities);
            amenities.setHotel(hotel);
        }

        if (request.getFreebies() != null) {
            Freebies freebies = (hotel.getFreebies() == null) ? new Freebies() : hotel.getFreebies();
            updateFreebiesEntity(freebies, request.getFreebies());
            hotel.setFreebies(freebies);
            freebies.setHotel(hotel);
        }

        // @Transactional 어노테이션으로 인해 메소드 종료 시 자동으로 dirty checking 되어 update 쿼리 실행
        return convertToHotelDto(hotel);
    }

    public void deleteHotel(Long hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new EntityNotFoundException("Hotel not found with id: " + hotelId);
        }
        hotelRepository.deleteById(hotelId);
    }

    // --- Helper Methods ---

    // Hotel 엔티티와 Request DTO 간의 필드 매핑 로직 (중복 제거)
    private void updateHotelEntityFromRequest(Hotel hotel, HotelRequestDto request) {
        hotel.setName(request.getName());
        hotel.setGrade(request.getGrade());
        hotel.setOverview(request.getOverview());
        hotel.setLatitude(request.getLatitude());
        hotel.setLongitude(request.getLongitude());
        hotel.setAddress(request.getAddress());
        hotel.setCheckinTime(request.getCheckinTime());
        hotel.setCheckoutTime(request.getCheckoutTime());
    }

    // 엔티티를 DTO로 변환하는 중앙 집중적 메소드
    private HotelDto convertToHotelDto(Hotel hotel) {
        AmenitiesDto amenitiesDto = null;
        if (hotel.getAmenities() != null) {
            Amenities a = hotel.getAmenities();
            amenitiesDto = new AmenitiesDto(a.isFrontDesk24(), a.isOutdoorPool(), a.isIndoorPool(), a.isSpaWellnessCenter(), a.isRestaurant(), a.isRoomservice(), a.isFitnessCenter(), a.isBarLounge(), a.isTeaCoffeeMachine(), a.isAirConditioner());
        }

        FreebiesDto freebiesDto = null;
        if (hotel.getFreebies() != null) {
            Freebies f = hotel.getFreebies();
            freebiesDto = new FreebiesDto(f.isBreakfastIncluded(), f.isFreeParking(), f.isFreeWifi(), f.isAirportShuttlebus(), f.isFreeCancellation());
        }

        String cityName = (hotel.getCity() != null) ? hotel.getCity().getCityName() : null;

        return new HotelDto(
                hotel.getId(), hotel.getName(), hotel.getGrade(), hotel.getOverview(), hotel.getLatitude(),
                hotel.getLongitude(), hotel.getAddress(), hotel.getCheckinTime(), hotel.getCheckoutTime(),
                cityName, amenitiesDto, freebiesDto
        );
    }

    private void updateAmenitiesEntity(Amenities amenities, AmenitiesDto dto) {
        amenities.setFrontDesk24(dto.isFrontDesk24());
        amenities.setOutdoorPool(dto.isOutdoorPool());
        amenities.setIndoorPool(dto.isIndoorPool());
        amenities.setSpaWellnessCenter(dto.isSpaWellnessCenter());
        amenities.setRestaurant(dto.isRestaurant());
        amenities.setRoomservice(dto.isRoomservice());
        amenities.setFitnessCenter(dto.isFitnessCenter());
        amenities.setBarLounge(dto.isBarLounge());
        amenities.setTeaCoffeeMachine(dto.isTeaCoffeeMachine());
        amenities.setAirConditioner(dto.isAirConditioner());
    }

    private void updateFreebiesEntity(Freebies freebies, FreebiesDto dto) {
        freebies.setBreakfastIncluded(dto.isBreakfastIncluded());
        freebies.setFreeParking(dto.isFreeParking());
        freebies.setFreeWifi(dto.isFreeWifi());
        freebies.setAirportShuttlebus(dto.isAirportShuttlebus());
        freebies.setFreeCancellation(dto.isFreeCancellation());
    }

    @Transactional(readOnly = true) // 조회이므로 readOnly = true
    public HotelDto getHotelById(Long hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + hotelId));

        // 이전에 만들어둔 변환 헬퍼 메소드를 재사용합니다.
        return convertToHotelDto(hotel);
    }
}