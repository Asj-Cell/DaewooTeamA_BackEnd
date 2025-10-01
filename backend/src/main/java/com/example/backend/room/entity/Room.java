package com.example.backend.room.entity;

import com.example.backend.hotel.entity.Hotel;
import com.example.backend.Reservation.Reservation;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity // <--- 추가
@Table(name = "room")
@NoArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_number", nullable = false, length = 10)
    private String roomNumber;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "view", length = 20)
    private String view;

    @Column(name = "bed", length = 50)
    private String bed;

    @Column(name = "max_guests", nullable = false)
    private Integer maxGuests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImg> images = new ArrayList<>();

}
