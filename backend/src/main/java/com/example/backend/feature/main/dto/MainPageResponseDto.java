package com.example.backend.feature.main.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 첫번쨰 메인 페이지
 * 모든 정보
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainPageResponseDto {
    private List<SearchDto> searchDtos;
    private List<PopularPackageDto> popularPackages;
    private MainPackageDto mainPackage;
}