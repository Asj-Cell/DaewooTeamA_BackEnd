package com.example.backend.amenities;

import com.example.backend.amenities.dto.AmenitiesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels/{hotelId}/amenities")
@RequiredArgsConstructor
public class AmenitiesController {

    private final AmenitiesService amenitiesService;

    /**
     * 특정 호텔의 편의시설 정보만 독립적으로 수정.
     * @param hotelId 호텔 ID
     * @param amenitiesDto 수정할 편의시설 정보
     * @return 수정된 편의시설 정보
     */
    @PutMapping
    public ResponseEntity<AmenitiesDto> updateAmenities(@PathVariable Long hotelId, @RequestBody AmenitiesDto amenitiesDto) {
        AmenitiesDto updatedAmenities = amenitiesService.updateAmenities(hotelId, amenitiesDto);
        return ResponseEntity.ok(updatedAmenities);
    }
}