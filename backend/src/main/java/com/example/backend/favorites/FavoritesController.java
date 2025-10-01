package com.example.backend.favorites;

import com.example.backend.feature.hotelfilters.dto.HotelDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "Favorites API", description = "✅ 인증 필요 | 찜 목록 관련 API")
@RequiredArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;

    @GetMapping("/api/favorites")
    public ResponseEntity<List<HotelDto>> getFavoriteHotels(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.parseLong(userDetails.getUsername());
        List<HotelDto> favoriteHotels = favoritesService.getFavoriteHotels(userId);
        return ResponseEntity.ok(favoriteHotels);
    }
}