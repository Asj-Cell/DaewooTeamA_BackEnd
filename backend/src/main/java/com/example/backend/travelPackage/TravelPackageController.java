package com.example.backend.travelPackage;

import com.example.backend.common.util.ApiResponse;
import com.example.backend.travelPackage.dto.MainPackageDto;
import com.example.backend.travelPackage.dto.PopularPackageDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Tag(name = "TravelPackage API", description = "여행 상품 관련 API") // http://localhost:8888/swagger-ui.html ㄱㄱㄱ
@RestController
@RequestMapping("/api/travel-packages")
@RequiredArgsConstructor
public class TravelPackageController {

    private final TravelPackageService travelPackageService;

    @Operation(summary = "인기 여행 상품 조회", description = "'여행에 빠지다' 섹션에 표시될 인기 여행 상품 4개를 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularPackageDto>>> getPopularPackages() {
        return ResponseEntity.ok(ApiResponse.success(travelPackageService.getPopularPackages()));
    }

    @Operation(summary = "메인 추천 상품 조회", description = "'여행 더보기' 섹션에 표시될 메인 추천 상품 1개를 조회합니다.")
    @GetMapping("/main")
    public ResponseEntity<ApiResponse<MainPackageDto>> getMainPackage() {
        return ResponseEntity.ok(ApiResponse.success(travelPackageService.getMainPackage()));
    }

}

