package com.example.backend.amenities;

import com.example.backend.amenities.entity.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmenitiesRepository extends JpaRepository<Amenities, Long> {


}
