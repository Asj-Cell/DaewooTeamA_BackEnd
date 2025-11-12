package com.example.backend.freebies;


import com.example.backend.freebies.dto.FreebiesDto;
import com.example.backend.freebies.entity.Freebies;
import com.example.backend.hotel.HotelRepository;
import com.example.backend.hotel.HotelService;
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
    private final FreebiesRepository freebiesRepository;
    private final HotelService hotelService;

    public FreebiesDto updateFreebies(Long hotelId, FreebiesDto freebiesDto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + hotelId));

        Freebies freebies = hotel.getFreebies();
        if (freebies == null) {
            freebies = new Freebies();
            hotel.setFreebies(freebies);
            freebies.setHotel(hotel);
        }

        hotelService.updateFreebiesEntity(freebies,freebiesDto);

        return new FreebiesDto(
                freebies.isBreakfastIncluded(), freebies.isFreeParking(), freebies.isFreeWifi(),
                freebies.isAirportShuttlebus(), freebies.isFreeCancellation()
        );
    }
}
