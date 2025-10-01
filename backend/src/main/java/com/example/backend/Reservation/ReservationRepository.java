package com.example.backend.Reservation;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Query("SELECT r FROM Reservation r " +
            "WHERE r.room.id = :roomId AND " +
            "r.checkinDate < :checkoutDate AND r.checkoutDate > :checkinDate")
    List<Reservation> findOverlappingReservations(
            @Param("roomId") Long roomId,
            @Param("checkinDate") LocalDate checkinDate,
            @Param("checkoutDate") LocalDate checkoutDate);
}
