package com.example.backend.room;

import com.example.backend.room.dto.RoomDto;
import com.example.backend.room.dto.RoomImgDto;
import com.example.backend.room.entity.Room;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

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
                .orElseThrow(() -> new RuntimeException("Room not found with id: " + id));
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
}
