package com.example.backend.travelPackage;

import com.example.backend.travelPackage.dto.MainPackageDto;
import com.example.backend.travelPackage.dto.PopularPackageDto;
import com.example.backend.travelPackage.entity.PackageImage;
import com.example.backend.travelPackage.entity.TravelPackage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPackageService {

    private final TravelPackageRepository travelPackageRepository;

    public List<PopularPackageDto> getPopularPackages() {
        List<TravelPackage> popularPackages = travelPackageRepository.findTop4ByOrderByIdDesc();

        return popularPackages.stream()
                .map(pkg -> {
                    String imageUrl = pkg.getImages().stream()
                            .findFirst()
                            .map(PackageImage::getImageUrl)
                            .orElse(null);

                    return new PopularPackageDto(
                            pkg.getId(),
                            pkg.getTitle(),
                            pkg.getCity().getCityName(),
                            pkg.getPrice(),
                            imageUrl
                    );
                })
                .toList();
    }


    public MainPackageDto getMainPackage() {
        return travelPackageRepository.findFirstByOrderByIdAsc()
                .map(pkg -> new MainPackageDto(
                        pkg.getId(),
                        pkg.getTitle(),
                        pkg.getDescription(),
                        pkg.getPrice(),
                        pkg.getImages().stream()
                                .map(img -> img.getImageUrl())
                                .toList()
                ))
                .orElse(null);
    }
}