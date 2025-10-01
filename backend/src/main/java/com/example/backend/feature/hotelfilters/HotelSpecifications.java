package com.example.backend.feature.hotelfilters;

import com.example.backend.amenities.entity.Amenities;
import com.example.backend.hotel.entity.Freebies;
import com.example.backend.hotel.entity.Hotel;
import com.example.backend.feature.hotelfilters.dto.HotelFilterRequestDto;
import com.example.backend.Reservation.Reservation;
import com.example.backend.room.entity.Room;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HotelSpecifications {

    public static Specification<Hotel> withFilters(HotelFilterRequestDto request) {
        return (root, query, cb) -> {
            query.distinct(true); // 중복 호텔 제거

            List<Predicate> predicates = new ArrayList<>();

            // === 도시 이름 ===
            if (request.getCityName() != null) {
                predicates.add(cb.equal(root.get("city").get("cityName"), request.getCityName()));
            }

            // === 객실, 예약정보 조인 ===
            Join<Hotel, Room> rooms = root.join("rooms", JoinType.LEFT);
            Join<Room, Reservation> reservations = rooms.join("reservations", JoinType.LEFT);

            // === 예약 날짜 필터 ===
            LocalDate checkIn = request.getCheckInDate();
            LocalDate checkOut = request.getCheckOutDate();

            if (checkIn != null && checkOut != null) {
                Predicate overlap = cb.and(
                        cb.lessThan(reservations.get("checkinDate"), checkOut),
                        cb.greaterThan(reservations.get("checkoutDate"), checkIn)
                );
                predicates.add(cb.or(cb.isNull(reservations.get("id")), cb.not(overlap)));
            } else {
                // 날짜가 null이면 예약 필터를 적용하지 않음
                // -> 모든 방 조회 가능
            }


            // === Freebies 필터 ===
            Join<Hotel, Freebies> freebies = root.join("freebies", JoinType.LEFT);
            if (Boolean.TRUE.equals(request.getBreakfastIncluded())) {
                predicates.add(cb.isTrue(freebies.get("breakfastIncluded")));
            }
            if (Boolean.TRUE.equals(request.getFreeParking())) {
                predicates.add(cb.isTrue(freebies.get("freeParking")));
            }
            if (Boolean.TRUE.equals(request.getFreeWifi())) {
                predicates.add(cb.isTrue(freebies.get("freeWifi")));
            }
            if (Boolean.TRUE.equals(request.getAirportShuttlebus())) {
                predicates.add(cb.isTrue(freebies.get("airportShuttlebus")));
            }
            if (Boolean.TRUE.equals(request.getFreeCancellation())) {
                predicates.add(cb.isTrue(freebies.get("freeCancellation")));
            }

            // === Amenities 필터 ===
            Join<Hotel, Amenities> amenities = root.join("amenities", JoinType.LEFT);
            if (Boolean.TRUE.equals(request.getFrontDesk24())) {
                predicates.add(cb.isTrue(amenities.get("frontDesk24")));
            }
            if (Boolean.TRUE.equals(request.getAirConditioner())) {
                predicates.add(cb.isTrue(amenities.get("airConditioner")));
            }
            if (Boolean.TRUE.equals(request.getFitnessCenter())) {
                predicates.add(cb.isTrue(amenities.get("fitnessCenter")));
            }
            if (Boolean.TRUE.equals(request.getPool())) {
                predicates.add(cb.or(
                        cb.isTrue(amenities.get("indoorPool")),
                        cb.isTrue(amenities.get("outdoorPool"))
                ));
            }

//            // === 등급 필터 === rating이 grade 인줄알고 실수
//            if (request.getMinGrade() != null) {
//                predicates.add(cb.greaterThanOrEqualTo(root.get("grade"), request.getMinGrade()));
//            }

            // === 방 갯수 필터 ===
            if (request.getMinAvailableRooms() != null) {
                predicates.add(cb.greaterThanOrEqualTo(rooms.get("maxGuests"), request.getMinAvailableRooms()));
            }



            // === 가격 필터 ===
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(rooms.get("price"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(rooms.get("price"), request.getMaxPrice()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
