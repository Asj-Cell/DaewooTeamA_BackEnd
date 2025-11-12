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
    @io.swagger.v3.oas.annotations.Operation(
        summary = "호텔 필터링 조회",
        description = """
            다양한 조건으로 호텔을 필터링하여 조회합니다.
            
            ⭐ 중요 기능:
            - checkInDate와 checkOutDate가 제공되면, 해당 기간에 예약된 방은 제외하고 최저가를 계산합니다.
            - 예: 1호텔의 30만원 방이 예약되어 있으면 → 35만원 방이 최저가로 표시됩니다.
            
            📌 날짜 겹침 로직:
            - 기존 예약이 10/10~10/15인 경우
            - 10/12~10/14 요청 → 겹침 (예약 불가)
            - 10/01~10/09 요청 → 가능
            - 10/16~10/20 요청 → 가능
            """
    )
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