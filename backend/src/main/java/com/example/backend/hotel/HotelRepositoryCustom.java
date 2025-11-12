package com.example.backend.hotel;

import com.example.backend.hotel.hotelfilters.dto.HotelFilterRequestDto;
import com.example.backend.hotel.hotelfilters.dto.HotelFiltersDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HotelRepositoryCustom {
    Page<HotelFiltersDto> findHotelsByFilters(HotelFilterRequestDto filter, Pageable pageable, Long loginUserId);
}
