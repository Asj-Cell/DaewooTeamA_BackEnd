package com.example.backend.hotel.hotelfilters;

// import com.example.backend.favorites.FavoritesRepository; // 필요 없으면 제거
import com.example.backend.hotel.entity.Hotel; // 사용하지 않으면 제거
import com.example.backend.hotel.hotelfilters.dto.HotelFiltersDto;
import com.example.backend.hotel.hotelfilters.dto.HotelFilterRequestDto;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.entity.HotelImage;
// import com.example.backend.room.entity.Room; // 사용하지 않으면 제거
// import com.example.backend.review.ReviewRepository; // 필요 없으면 제거

// --- 이미지 URL 조회를 위한 Import 추가 ---
import com.example.backend.hotel.HotelImageRepository; // HotelImageRepository 추가 (가정)
import jakarta.persistence.EntityNotFoundException; // 필요시 추가

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl; // PageImpl 추가
import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort; // 사용하지 않으면 제거
// import org.springframework.data.jpa.domain.Specification; // 사용하지 않으면 제거
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal; // 사용하지 않으면 제거
import java.time.LocalDate; // 사용하지 않으면 제거
import java.util.Collections; // Collections 추가
import java.util.Comparator; // 사용하지 않으면 제거
import java.util.List;
import java.util.Map; // Map 추가
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션으로 변경 (이미지 조회만 추가)
public class HotelFiltersService {

    private final HotelRepository hotelRepository;
    private final HotelImageRepository hotelImageRepository; // HotelImageRepository 주입

    @Transactional // 이미지 URL을 DTO에 다시 설정해야 하므로 readOnly = false 유지 (또는 별도 메소드 분리)
    public Page<HotelFiltersDto> filterHotels(HotelFilterRequestDto request, Pageable pageable, Long loginUserId) {
        // 1. 리포지토리에서 이미지 URL을 제외한 기본 DTO 조회
        Page<HotelFiltersDto> initialResult = hotelRepository.findHotelsByFilters(request, pageable, loginUserId);

        List<HotelFiltersDto> content = initialResult.getContent();

        // 2. 조회된 호텔 ID 목록 추출
        List<Long> hotelIds = content.stream()
                .map(HotelFiltersDto::getId)
                .toList();

        // 3. 호텔 ID 목록으로 이미지 URL 조회 (N+1 문제 방지)
        Map<Long, List<String>> imagesMap = Collections.emptyMap(); // 기본값 빈 맵
        if (!hotelIds.isEmpty()) {
            // HotelImage 엔티티에서 해당 호텔 ID들에 속하는 모든 이미지를 조회 (sequence 순으로 정렬)
            List<HotelImage> images = hotelImageRepository.findByHotelIdInOrderBySequenceAsc(hotelIds); // 이 메소드가 필요

            // 호텔 ID별로 이미지 URL 리스트를 그룹화
            imagesMap = images.stream()
                    .collect(Collectors.groupingBy(
                            img -> img.getHotel().getId(), // HotelImage 엔티티에 getHotel()이 있다고 가정
                            Collectors.mapping(HotelImage::getImageUrl, Collectors.toList())
                    ));
        }

        // 4. 기존 DTO 리스트를 순회하며 이미지 URL 채우기 (새로운 DTO 리스트 생성)
        Map<Long, List<String>> finalImagesMap = imagesMap; // effectively final
        List<HotelFiltersDto> contentWithImages = content.stream()
                .map(dto -> new HotelFiltersDto( // 기존 DTO 정보를 사용하여 새 DTO 생성
                        dto.getId(),
                        dto.getName(),
                        dto.getAddress(),
                        dto.getGrade(),
                        dto.getAmenitiesCount(),
                        dto.getPrice(),
                        dto.getRating(),
                        // 해당 호텔 ID의 이미지 URL 리스트를 Map에서 찾아 설정 (없으면 빈 리스트)
                        finalImagesMap.getOrDefault(dto.getId(), Collections.emptyList()),
                        dto.getFavoriteId(),
                        dto.getReviewCount()
                ))
                .toList();

        // 5. 이미지 URL이 채워진 새로운 DTO 리스트로 Page 객체 재생성
        return new PageImpl<>(contentWithImages, initialResult.getPageable(), initialResult.getTotalElements());
    }
}