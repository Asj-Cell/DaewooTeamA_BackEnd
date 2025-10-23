package com.example.backend.hotel.hotelfilters;

import com.example.backend.hotel.hotelfilters.dto.HotelFiltersDto;
import com.example.backend.hotel.hotelfilters.dto.HotelFilterRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
            @ModelAttribute HotelFilterRequestDto requestDto,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size
    ) {
        Long loginUserId = (userDetails != null) ? Long.parseLong(userDetails.getUsername()) : null;
        Pageable pageable = PageRequest.of(page, size);
        Page<HotelFiltersDto> hotelPage = hotelFiltersService.filterHotels(requestDto, pageable, loginUserId);

        Map<String, Object> response = new HashMap<>();
        response.put("hotels", hotelPage.getContent());
        response.put("totalHotels", hotelPage.getTotalElements());
        response.put("currentPage", hotelPage.getNumber());
        response.put("totalPages", hotelPage.getTotalPages());

        return response;
    }
}