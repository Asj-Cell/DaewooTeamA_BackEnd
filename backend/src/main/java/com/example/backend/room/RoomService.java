package com.example.backend.room;

import com.example.backend.common.exception.HotelException;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.room.dto.RoomDto;
import com.example.backend.room.dto.RoomImgDto;
import com.example.backend.room.dto.RoomRequestDto;
import com.example.backend.room.entity.Room;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;


    /**
     * 특정 호텔에 새로운 객실을 생성합니다.
     * @param hotelId 객실을 추가할 호텔의 ID
     * @param request 생성할 객실 정보 DTO
     * @return 생성된 객실 정보 DTO
     */
    public RoomDto createRoom(Long hotelId, RoomRequestDto request) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() ->
                        HotelException.HOTEL_NOT_FOUND.getException());

        Room room = new Room();
        updateRoomEntityFromRequest(room, request);
        room.setHotel(hotel); // 부모 호텔과의 연관관계 설정

        Room savedRoom = roomRepository.save(room);
        return convertToDto(savedRoom);
    }

    /**
     * 기존 객실의 정보를 수정합니다.
     * @param roomId 수정할 객실의 ID
     * @param request 수정할 내용이 담긴 DTO
     * @return 수정된 객실 정보 DTO
     */
    public RoomDto updateRoom(Long roomId, RoomRequestDto request) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() ->
                        HotelException.ROOM_NOT_FOUND.getException());
        updateRoomEntityFromRequest(room, request);
        // @Transactional에 의해 메소드 종료 시 자동 업데이트
        return convertToDto(room);
    }

    /**
     * 특정 객실을 삭제합니다.
     * @param roomId 삭제할 객실의 ID
     */
    public void deleteRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw HotelException.ROOM_NOT_FOUND.getException();
        }
        roomRepository.deleteById(roomId);
    }
    // 특정 호텔의 모든 룸 조회
    public List<RoomDto> getRoomsByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // 특정 룸 조회
    public RoomDto getRoomById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() ->
                        HotelException.ROOM_NOT_FOUND.getException());
        return convertToDto(room);
    }

    // Room -> RoomDto 변환 (필요한 정보만)
    private RoomDto convertToDto(Room room) {
        // DTO에 @NoArgsConstructor와 @Setter가 있으므로,
        // setter를 이용해 값을 설정하는 방식으로 코드를 수정합니다.
        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setName(room.getName());
        dto.setPrice(room.getPrice());
        dto.setView(room.getView());
        dto.setBed(room.getBed());
        dto.setMaxGuests(room.getMaxGuests());
        dto.setRoomImages(
                room.getImages().stream()
                        .map(img -> new RoomImgDto(img.getId(), img.getImageUrl(), img.getSize()))
                        .collect(Collectors.toList())
        );
        // isAvailable은 날짜 정보가 없으므로 여기서는 null로 설정합니다.
        dto.setIsAvailable(null);

        return dto;
    }

    private void updateRoomEntityFromRequest(Room room, RoomRequestDto request) {
        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setName(request.getName());
        room.setView(request.getView());
        room.setBed(request.getBed());
        room.setMaxGuests(request.getMaxGuests());
    }
}
