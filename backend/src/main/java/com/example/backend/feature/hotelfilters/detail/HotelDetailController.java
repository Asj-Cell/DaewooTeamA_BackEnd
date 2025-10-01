package com.example.backend.feature.hotelfilters.detail;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hotels/detail")
public class HotelDetailController {

    private final HotelDetailService hotelDetailService;

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDetailDto> getHotelDetail(
            @PathVariable Long hotelId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long loginUserId = (userDetails != null) ? Long.parseLong(userDetails.getUsername()) : null;
        HotelDetailDto detailDto = hotelDetailService.getHotelDetail(hotelId, loginUserId, checkInDate, checkOutDate);
        return ResponseEntity.ok(detailDto);
    }
}