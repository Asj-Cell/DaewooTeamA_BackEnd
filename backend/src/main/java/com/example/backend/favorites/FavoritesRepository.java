package com.example.backend.favorites;

import com.example.backend.favorites.entity.Favorites;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    boolean existsByUser_IdAndHotel_Id(Long userId, Long hotelId);
    Page<Favorites> findAllByUser_Id(Long userId, Pageable pageable);
    Optional<Favorites> findByUser_IdAndHotel_Id(Long userId, Long hotelId);
}

