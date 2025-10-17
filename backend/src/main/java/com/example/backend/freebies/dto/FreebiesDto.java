package com.example.backend.freebies.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FreebiesDto {
    private boolean breakfastIncluded;
    private boolean freeParking;
    private boolean freeWifi;
    private boolean airportShuttlebus;
    private boolean freeCancellation;
}
