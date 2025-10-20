package com.example.backend.freebies;

import com.example.backend.freebies.dto.FreebiesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels/{hotelId}/freebies")
@RequiredArgsConstructor
public class FreebiesController {

    private final FreebiesService freebiesService;

    /**
     * 특정 호텔의 무료 서비스 정보만 독립적으로 수정.
     * @param hotelId 호텔 ID
     * @param freebiesDto 수정할 무료 서비스 정보
     * @return 수정된 무료 서비스 정보
     */
    @PutMapping
    public ResponseEntity<FreebiesDto> updateFreebies(@PathVariable Long hotelId, @RequestBody FreebiesDto freebiesDto) {
        FreebiesDto updatedFreebies = freebiesService.updateFreebies(hotelId, freebiesDto);
        return ResponseEntity.ok(updatedFreebies);
    }
}