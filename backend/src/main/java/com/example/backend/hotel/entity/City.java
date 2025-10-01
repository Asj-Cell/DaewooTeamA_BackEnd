package com.example.backend.hotel.entity;

import com.example.backend.travelPackage.entity.TravelPackage; // 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList; // 추가
import java.util.List;       // 추가

@Getter @Setter
@Entity
@Table(name = "city")
@NoArgsConstructor
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "city_name", nullable = false, length = 20)
    private String cityName;

    // --- 아래 내용 추가 ---

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hotel> hotels = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelPackage> travelPackages = new ArrayList<>();
}