package com.example.backend.freebies;

import com.example.backend.amenities.entity.Amenities;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FreebiesRepository extends JpaRepository<Amenities, Long> {


}
