package com.example.backend.amenities.entity;


import com.example.backend.hotel.entity.Hotel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "amenities")
@NoArgsConstructor
public class Amenities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "front_desk24")
    private boolean frontDesk24;

    @Column(name = "outdoor_pool")
    private boolean outdoorPool;

    @Column(name = "indoor_pool")
    private boolean indoorPool;

    @Column(name = "spa_wellness_center")
    private boolean spaWellnessCenter;

    @Column(name = "restaurant")
    private boolean restaurant;

    @Column(name = "roomservice")
    private boolean roomservice;

    @Column(name = "fitness_center")
    private boolean fitnessCenter;

    @Column(name = "bar_lounge")
    private boolean barLounge;

    @Column(name = "tea_coffee_machine")
    private boolean teaCoffeeMachine;

    @Column(name = "airconditioning")
    private boolean airConditioner;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
