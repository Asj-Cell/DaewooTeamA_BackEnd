package com.example.backend.room;

import com.example.backend.room.dto.RoomDto;
import com.example.backend.room.dto.RoomRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
// URL 설계를 RESTful하게 변경합니다.
// 특정 호텔에 종속된 방은 /api/hotels/{hotelId}/rooms 로,
// ID로 직접 접근 가능한 방은 /api/rooms/{roomId} 로 구분합니다.
@RequestMapping("/api")
public class RoomController {

    private final RoomService roomService;

    // === CUD API ===

    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<RoomDto> createRoom(@PathVariable Long hotelId, @RequestBody RoomRequestDto request) {
        RoomDto createdRoom = roomService.createRoom(hotelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(@PathVariable Long roomId, @RequestBody RoomRequestDto request) {
        RoomDto updatedRoom = roomService.updateRoom(roomId, request);
        return ResponseEntity.ok(updatedRoom);
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    // === Read API ===

    @GetMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<List<RoomDto>> getRoomsByHotelId(@PathVariable Long hotelId) {
        List<RoomDto> rooms = roomService.getRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDto> getRoomById(@PathVariable Long roomId) {
        RoomDto room = roomService.getRoomById(roomId);
        return ResponseEntity.ok(room);
    }
}
