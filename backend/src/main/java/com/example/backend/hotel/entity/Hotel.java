package com.example.backend.hotel.entity;

import com.example.backend.amenities.entity.Amenities;
import com.example.backend.room.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@NoArgsConstructor
@Table(name = "hotel")
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "overview", length = 100, nullable = false)
    private String overview;

    @Column(name = "latitude", nullable = false)
    private double latitude;

    @Column(name = "longitude", nullable = false)
    private double longitude;

    @Column(name = "address", nullable = false, length = 100)
    private String address;

    @Column(name = "checkin_time", nullable = false)
    private LocalTime checkinTime;

    @Column(name = "checkout_time", nullable = false)
    private LocalTime checkoutTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HotelImage> images = new ArrayList<>();

    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL)
    private Freebies freebies;

    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL)
    private Amenities amenities;
}
