package com.example.backend.feature.hotelfilters;

import com.example.backend.feature.hotelfilters.dto.HotelDto;
import com.example.backend.feature.hotelfilters.dto.HotelFilterRequestDto;
import com.example.backend.user.dto.UserProfileRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels")
public class HotelFiltersController {
    private final HotelFiltersService hotelFiltersService;

    //모든 편의시설 false, cityName= x, url로 페이지랑 사이즈를 받는데 기본값은 페이지 0이고 사이지는 4
    //http://localhost:8888/api/hotels/filter?page=0&size=4&sortBy=rating&breakfastIncluded=false&freeParking=false&freeWifi=false&airportShuttlebus=false&freeCancellation=false&frontDesk24=false&airConditioner=false&fitnessCenter=false&pool=false&checkInDate=2025-10-01&checkOutDate=2025-10-05
    @GetMapping("/filter")
    public Map<String, Object> filterHotels(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String cityName,
            @RequestParam(required = false) Boolean breakfastIncluded,
            @RequestParam(required = false) Boolean freeParking,
            @RequestParam(required = false) Boolean freeWifi,
            @RequestParam(required = false) Boolean airportShuttlebus,
            @RequestParam(required = false) Boolean freeCancellation,
            @RequestParam(required = false) Boolean frontDesk24,
            @RequestParam(required = false) Boolean airConditioner,
            @RequestParam(required = false) Boolean fitnessCenter,
            @RequestParam(required = false) Boolean pool,
            @RequestParam(required = false) Integer minAvgRating,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minAvailableRooms,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String checkInDate,
            @RequestParam(required = false) String checkOutDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Long loginUserId = (userDetails != null) ? Long.parseLong(userDetails.getUsername()) : null;
        Pageable pageable = PageRequest.of(page, size);
        // DTO로 변환
        HotelFilterRequestDto request = new HotelFilterRequestDto();
        request.setCityName(cityName);
        request.setBreakfastIncluded(breakfastIncluded);
        request.setFreeParking(freeParking);
        request.setFreeWifi(freeWifi);
        request.setAirportShuttlebus(airportShuttlebus);
        request.setFreeCancellation(freeCancellation);
        request.setFrontDesk24(frontDesk24);
        request.setAirConditioner(airConditioner);
        request.setFitnessCenter(fitnessCenter);
        request.setPool(pool);
        request.setMinAvgRating(minAvgRating);
        request.setMinPrice(minPrice);
        request.setMaxPrice(maxPrice);
        request.setMinAvailableRooms(minAvailableRooms);
        request.setSortBy(sortBy);

        if (checkInDate != null) request.setCheckInDate(LocalDate.parse(checkInDate));
        if (checkOutDate != null) request.setCheckOutDate(LocalDate.parse(checkOutDate));

        Page<HotelDto> hotelPage = hotelFiltersService.filterHotels(request, pageable, loginUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("hotels", hotelPage.getContent());
        response.put("totalHotels", hotelPage.getTotalElements());
        response.put("currentPage", hotelPage.getNumber());
        response.put("totalPages", hotelPage.getTotalPages());

        return response;
    }
}