package com.example.backend.favorites;

import com.example.backend.favorites.entity.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    boolean existsByUser_IdAndHotel_Id(Long userId, Long hotelId);
    List<Favorites> findAllByUser_Id(Long userId);
}

