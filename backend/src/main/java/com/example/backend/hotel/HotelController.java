package com.example.backend.hotel;

import com.example.backend.hotel.dto.HotelDto;
import com.example.backend.hotel.dto.HotelRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    //특정 호텔을 검색하는
    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId) {
        HotelDto hotelDto = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(hotelDto);
    }
    //호텔 만들기
    @PostMapping
    public ResponseEntity<HotelDto> createHotel(@RequestBody HotelRequestDto request) {
        HotelDto createdHotel = hotelService.createHotel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdHotel);
    }
    //호텔 수정
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotel(@PathVariable Long hotelId, @RequestBody HotelRequestDto request) {
        HotelDto updatedHotel = hotelService.updateHotel(hotelId, request);
        return ResponseEntity.ok(updatedHotel);
    }
    //호텔 삭제
    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotel(@PathVariable Long hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.noContent().build();
    }
}