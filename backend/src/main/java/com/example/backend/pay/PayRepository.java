package com.example.backend.pay;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PayRepository extends JpaRepository<Pay, Long> {
    Optional<Pay> findByReservation_Id(Long reservationId);
}
