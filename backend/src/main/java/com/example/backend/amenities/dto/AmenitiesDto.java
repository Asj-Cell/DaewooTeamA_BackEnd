package com.example.backend.amenities.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmenitiesDto {
    private boolean frontDesk24;
    private boolean outdoorPool;
    private boolean indoorPool;
    private boolean spaWellnessCenter;
    private boolean restaurant;
    private boolean roomservice;
    private boolean fitnessCenter;
    private boolean barLounge;
    private boolean teaCoffeeMachine;
    private boolean airConditioner;
}