package com.example.backend.freebies;


import com.example.backend.freebies.dto.FreebiesDto;
import com.example.backend.freebies.entity.Freebies;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.entity.Hotel;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class FreebiesService {

    private final HotelRepository hotelRepository;

    public FreebiesDto updateFreebies(Long hotelId, FreebiesDto freebiesDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + hotelId));

        Freebies freebies = hotel.getFreebies();
        if (freebies == null) {
            freebies = new Freebies();
            hotel.setFreebies(freebies);
        }

        freebies.setBreakfastIncluded(freebiesDto.isBreakfastIncluded());
        freebies.setFreeParking(freebiesDto.isFreeParking());
        freebies.setFreeWifi(freebiesDto.isFreeWifi());
        freebies.setAirportShuttlebus(freebiesDto.isAirportShuttlebus());
        freebies.setFreeCancellation(freebiesDto.isFreeCancellation());

        return new FreebiesDto(
                freebies.isBreakfastIncluded(), freebies.isFreeParking(), freebies.isFreeWifi(),
                freebies.isAirportShuttlebus(), freebies.isFreeCancellation()
        );
    }
}
