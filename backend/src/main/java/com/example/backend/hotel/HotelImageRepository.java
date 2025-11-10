package com.example.backend.hotel;

import com.example.backend.hotel.entity.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HotelImageRepository extends JpaRepository<HotelImage, Long> {
    List<HotelImage> findByHotelIdInOrderBySequenceAsc(List<Long> hotelIds);
}

