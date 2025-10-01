package com.example.backend.hotel.entity;

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

    @Column(name = "Breakfast_included")
    private boolean breakfastIncluded;

    @Column(name = "free_parking")
    private boolean freeParking;

    @Column(name = "free_wifi")
    private boolean freeWifi;

    @Column(name = "Airport_shuttlebus")
    private boolean airportShuttlebus;

    @Column(name = "free_cancellation")
    private boolean freeCancellation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;

}
