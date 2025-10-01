package com.example.backend.travelPackage;

import com.example.backend.travelPackage.entity.TravelPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long>  {

    // 여행에 빠지다 섹션을 위한 쿼리 메서드 (최신 4개) // 일단 id 역순으로 4개 ;;
    // SELECT * FROM travel_package ORDER BY id DESC LIMIT 4; 애랑 같음
    List<TravelPackage> findTop4ByOrderByIdDesc();

    // 여행 더보기 섹션을 위한 쿼리 메서드 (최신 1개)  // 일단 id 역순으로 1개 ;;
    // SELECT * FROM travel_package ORDER BY id ASC LIMIT 1; 애랑 같음
    Optional<TravelPackage> findFirstByOrderByIdAsc();
}
