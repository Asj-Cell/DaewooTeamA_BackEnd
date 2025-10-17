package com.example.backend.freebies.entity;

import com.example.backend.hotel.entity.Hotel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "freebies")
@NoArgsConstructor
public class Freebies {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "breakfast_included")
    private boolean breakfastIncluded;

    @Column(name = "free_parking")
    private boolean freeParking;

    @Column(name = "free_wifi")
    private boolean freeWifi;

    @Column(name = "airport_shuttlebus")
    private boolean airportShuttlebus;

    @Column(name = "free_cancellation")
    private boolean freeCancellation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

}
