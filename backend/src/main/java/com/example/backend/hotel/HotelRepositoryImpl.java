package com.example.backend.hotel;

import com.example.backend.hotel.entity.City;
import com.example.backend.hotel.entity.QCity;
import com.example.backend.hotel.hotelfilters.dto.HotelFilterRequestDto;
import com.example.backend.hotel.hotelfilters.dto.HotelFiltersDto;
import com.example.backend.hotel.hotelfilters.dto.QHotelFiltersDto;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

// QClass static import
import static com.example.backend.Reservation.QReservation.reservation;
import static com.example.backend.hotel.entity.QCity.city;
import static com.example.backend.room.entity.QRoom.room;
import static com.example.backend.amenities.entity.QAmenities.amenities;
import static com.example.backend.freebies.entity.QFreebies.freebies;
import static com.example.backend.hotel.entity.QHotel.hotel;
import static com.example.backend.review.entity.QReview.review;
import static com.example.backend.favorites.entity.QFavorites.favorites;

// 서브쿼리용 별칭 QClass
import com.example.backend.room.entity.QRoom;
import com.example.backend.Reservation.QReservation;


@Repository
@RequiredArgsConstructor
public class HotelRepositoryImpl implements HotelRepositoryCustom {

    // 서브쿼리용 별칭 인스턴스
    private static final QRoom subRoomForMinPrice = new QRoom("subRoomForMinPrice");
    private static final QRoom subRoomForAvail = new QRoom("subRoomForAvail");
    private static final QRoom subRoomForGuest = new QRoom("subRoomForGuest");

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HotelFiltersDto> findHotelsByFilters(HotelFilterRequestDto filter, Pageable pageable, Long loginUserId) {

        final Long userId = loginUserId != null ? loginUserId : -1L;

        try {
            // 리뷰 평균 계산 (호텔 그룹별)
            NumberExpression<Double> avgRating = review.userRatingScore.avg().coalesce(0.0);

            // 리뷰 개수 계산 (호텔 그룹별)
            NumberExpression<Long> reviewCount = review.countDistinct();

            // 최소 가격 계산 (호텔 그룹별, 예약 가능한 방만 대상)
            BooleanExpression minPriceSubQueryCondition = subRoomForMinPrice.hotel.id.eq(hotel.id);
            if (filter.getCheckInDate() != null && filter.getCheckOutDate() != null) {
                minPriceSubQueryCondition = minPriceSubQueryCondition.and(
                        isRoomAvailableSubquery(subRoomForMinPrice, filter.getCheckInDate(), filter.getCheckOutDate()).notExists()
                );
            }
            NumberExpression<BigDecimal> minAvailablePriceExpr = Expressions.numberOperation(
                    BigDecimal.class, Ops.COALESCE,
                    JPAExpressions.select(subRoomForMinPrice.price.min())
                            .from(subRoomForMinPrice)
                            .where(minPriceSubQueryCondition),
                    Expressions.constant(BigDecimal.ZERO)
            );

            // 편의시설 개수 계산 (호텔 그룹별)
            NumberExpression<Integer> amenitiesCountExpr = calculateAmenitiesCount().as("amenitiesCount");

            // 찜 여부 (호텔 그룹별)
            BooleanExpression isFavoriteExpr = isFavoriteSubquery(userId).as("isFavorite");

            // 메인 쿼리
            JPAQuery<HotelFiltersDto> query = queryFactory
                    .select(new QHotelFiltersDto(
                            hotel.id,
                            hotel.name,
                            hotel.address,
                            hotel.grade,
                            amenitiesCountExpr,
                            minAvailablePriceExpr,
                            avgRating,
                            Expressions.constant(Collections.<String>emptyList()),
                            isFavoriteExpr,
                            reviewCount,
                            city.cityName,
                            city.country
                    ))
                    .from(hotel)
                    .leftJoin(hotel.city, city)
                    .leftJoin(hotel.reviews, review)
                    .leftJoin(hotel.freebies, freebies)
                    .leftJoin(hotel.amenities, amenities)
                    .leftJoin(hotel.rooms, room)
                    .groupBy(hotel.id, hotel.name, hotel.address, hotel.grade, city.cityName, city.country);

            // WHERE 절 필터 조건 적용
            BooleanExpression conditions = createConditions(filter);
            if (conditions != null) {
                query.where(conditions);
            }

            // HAVING 절: 계산된 값 기준 필터링
            if (filter.getMinAvgRating() != null) {
                query.having(avgRating.goe(filter.getMinAvgRating().doubleValue()));
            }

            // 가격 범위 필터를 HAVING 절에 추가
            if (filter.getMinPrice() != null) {
                query.having(minAvailablePriceExpr.goe(filter.getMinPrice()));
            }
            if (filter.getMaxPrice() != null) {
                query.having(minAvailablePriceExpr.loe(filter.getMaxPrice()));
            }

            // 정렬 적용
            OrderSpecifier<?>[] orderSpecifier = getOrderSpecifier(filter.getSortBy(), avgRating, minAvailablePriceExpr);
            if (orderSpecifier != null) {
                query.orderBy(orderSpecifier);
            }

            // 페이징 적용
            List<HotelFiltersDto> content = query
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            // ✅ 카운트 쿼리 - HAVING 절을 사용하므로 특별한 처리 필요
            // 조건을 만족하는 hotel.id 목록을 가져온 후 개수를 셈
            JPAQuery<Long> countQuery = queryFactory
                    .select(hotel.id)
                    .from(hotel)
                    .leftJoin(hotel.city, city)
                    .leftJoin(hotel.rooms, room)
                    .leftJoin(hotel.reviews, review)
                    .leftJoin(hotel.freebies, freebies)
                    .leftJoin(hotel.amenities, amenities)
                    .groupBy(hotel.id);

            if (conditions != null) {
                countQuery.where(conditions);
            }

            // HAVING 절 조건 적용
            if (filter.getMinAvgRating() != null) {
                NumberExpression<Double> countAvgRating = review.userRatingScore.avg().coalesce(0.0);
                countQuery.having(countAvgRating.goe(filter.getMinAvgRating().doubleValue()));
            }

            // 가격 필터
            if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
                NumberExpression<BigDecimal> countMinPriceExpr = Expressions.numberOperation(
                        BigDecimal.class, Ops.COALESCE,
                        JPAExpressions.select(subRoomForMinPrice.price.min())
                                .from(subRoomForMinPrice)
                                .where(minPriceSubQueryCondition),
                        Expressions.constant(BigDecimal.ZERO)
                );

                if (filter.getMinPrice() != null) {
                    countQuery.having(countMinPriceExpr.goe(filter.getMinPrice()));
                }
                if (filter.getMaxPrice() != null) {
                    countQuery.having(countMinPriceExpr.loe(filter.getMaxPrice()));
                }
            }

            // ✅ HAVING 절 사용 시 개수를 세는 올바른 방법
            List<Long> filteredHotelIds = countQuery.fetch();
            long totalCount = filteredHotelIds.size();

            return new PageImpl<>(content, pageable, totalCount);

        } catch (Exception e) {
            throw new RuntimeException("Error while querying hotels: " + e.getMessage(), e);
        }
    }

    // --- Select 절용 헬퍼 ---
    private BooleanExpression isFavoriteSubquery(Long userId) {
        if (userId == null || userId < 0) {
            return Expressions.asBoolean(false);
        }
        return JPAExpressions.selectOne()
                .from(favorites)
                .where(favorites.hotel.id.eq(hotel.id)
                        .and(favorites.user.id.eq(userId)))
                .exists();
    }

    private NumberExpression<Integer> calculateAmenitiesCount() {
        return new CaseBuilder().when(freebies.breakfastIncluded.isTrue()).then(1).otherwise(0)
                .add(new CaseBuilder().when(freebies.freeParking.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(freebies.freeWifi.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(freebies.airportShuttlebus.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(freebies.freeCancellation.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.frontDesk24.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.airConditioner.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.fitnessCenter.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.indoorPool.isTrue().or(amenities.outdoorPool.isTrue())).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.spaWellnessCenter.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.restaurant.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.roomservice.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.barLounge.isTrue()).then(1).otherwise(0))
                .add(new CaseBuilder().when(amenities.teaCoffeeMachine.isTrue()).then(1).otherwise(0));
    }

    // --- Where 절용 헬퍼 (호텔 필터링) ---
    private BooleanExpression createConditions(HotelFilterRequestDto filter) {
        BooleanExpression conditions = null;

        // 도시명 또는 호텔명 검색
        if (filter.getCityName() != null && !filter.getCityName().trim().isEmpty()) {
            String searchTerm = filter.getCityName().trim();
            conditions = and(conditions,
                    hotel.city.cityName.containsIgnoreCase(searchTerm)
                            .or(hotel.name.containsIgnoreCase(searchTerm))
            );
        }

        // 날짜 기반 호텔 필터링
        if (filter.getCheckInDate() != null && filter.getCheckOutDate() != null) {
            conditions = and(conditions, hotelHasAvailableRoom(filter.getCheckInDate(), filter.getCheckOutDate()));
        }

        // 최소 수용 인원 필터
        if (filter.getMinAvailableRooms() != null) {
            conditions = and(conditions, hotelHasRoomWithMinGuests(filter.getMinAvailableRooms()));
        }

        // 편의시설 필터
        conditions = and(conditions, hasBreakfast(filter.getBreakfastIncluded()));
        conditions = and(conditions, hasFreeParking(filter.getFreeParking()));
        conditions = and(conditions, hasFreeWifi(filter.getFreeWifi()));
        conditions = and(conditions, hasAirportShuttle(filter.getAirportShuttlebus()));
        conditions = and(conditions, hasFreeCancellation(filter.getFreeCancellation()));
        conditions = and(conditions, hasFrontDesk24(filter.getFrontDesk24()));
        conditions = and(conditions, hasAirConditioner(filter.getAirConditioner()));
        conditions = and(conditions, hasFitnessCenter(filter.getFitnessCenter()));
        conditions = and(conditions, hasPool(filter.getPool()));

        return conditions;
    }

    private BooleanExpression and(BooleanExpression source, BooleanExpression expression) {
        if (expression == null) return source;
        return source == null ? expression : source.and(expression);
    }

    // --- 서브쿼리 조건 헬퍼 ---

    /**
     * 특정 방이 주어진 체크인-체크아웃 기간에 예약되어 있는지 확인하는 서브쿼리
     *
     * 📌 날짜 겹침 로직:
     * - 예약의 체크인 < 요청 체크아웃 AND 예약의 체크아웃 > 요청 체크인
     * - 이 조건을 만족하면 예약이 "겹친다"고 판단
     */
    private JPQLQuery<Integer> isRoomAvailableSubquery(QRoom roomAlias, LocalDate checkIn, LocalDate checkOut) {
        String resAliasName = roomAlias.toString().replace(".", "_") + "_res";
        QReservation resAlias = new QReservation(resAliasName);
        return JPAExpressions.selectOne()
                .from(resAlias)
                .where(resAlias.room.eq(roomAlias),
                        resAlias.checkinDate.lt(checkOut),
                        resAlias.checkoutDate.gt(checkIn)
                );
    }

    // [체크인-체크아웃] 기간에 예약 가능한 방을 하나라도 가졌는지
    private BooleanExpression hotelHasAvailableRoom(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) return Expressions.asBoolean(true).isTrue();
        return JPAExpressions.selectOne()
                .from(subRoomForAvail)
                .where(subRoomForAvail.hotel.id.eq(hotel.id),
                        isRoomAvailableSubquery(subRoomForAvail, checkIn, checkOut).notExists()
                )
                .exists();
    }

    // 호텔이 특정 최소 수용 인원을 만족하는 방을 가지고 있는지
    private BooleanExpression hotelHasRoomWithMinGuests(Integer minGuests) {
        if (minGuests == null) return Expressions.asBoolean(true).isTrue();
        return JPAExpressions.selectOne()
                .from(subRoomForGuest)
                .where(subRoomForGuest.hotel.id.eq(hotel.id),
                        subRoomForGuest.maxGuests.goe(minGuests))
                .exists();
    }

    // --- 편의시설 / 무료혜택 필터 ---
    private BooleanExpression hasBreakfast(Boolean value) {
        return value != null && value ? freebies.breakfastIncluded.isTrue() : null;
    }

    private BooleanExpression hasFreeParking(Boolean value) {
        return value != null && value ? freebies.freeParking.isTrue() : null;
    }

    private BooleanExpression hasFreeWifi(Boolean value) {
        return value != null && value ? freebies.freeWifi.isTrue() : null;
    }

    private BooleanExpression hasAirportShuttle(Boolean value) {
        return value != null && value ? freebies.airportShuttlebus.isTrue() : null;
    }

    private BooleanExpression hasFreeCancellation(Boolean value) {
        return value != null && value ? freebies.freeCancellation.isTrue() : null;
    }

    private BooleanExpression hasFrontDesk24(Boolean value) {
        return value != null && value ? amenities.frontDesk24.isTrue() : null;
    }

    private BooleanExpression hasAirConditioner(Boolean value) {
        return value != null && value ? amenities.airConditioner.isTrue() : null;
    }

    private BooleanExpression hasFitnessCenter(Boolean value) {
        return value != null && value ? amenities.fitnessCenter.isTrue() : null;
    }

    private BooleanExpression hasPool(Boolean value) {
        return value != null && value ? amenities.indoorPool.isTrue().or(amenities.outdoorPool.isTrue()) : null;
    }

    // --- 정렬 ---
    private OrderSpecifier<?>[] getOrderSpecifier(String sortBy,
                                                  NumberExpression<Double> avgRatingExpr,
                                                  NumberExpression<BigDecimal> minAvailablePriceExpr) {
        String sort = sortBy != null ? sortBy.trim().toLowerCase() : "";
        switch (sort) {
            case "rating":
                return new OrderSpecifier[]{ avgRatingExpr.desc().nullsLast(), hotel.id.asc() };
            case "priceasc":
                return new OrderSpecifier[]{ minAvailablePriceExpr.asc().nullsLast(), hotel.id.asc() };
            case "pricedesc":
                return new OrderSpecifier[]{ minAvailablePriceExpr.desc().nullsLast(), hotel.id.asc() };
            default:
                return new OrderSpecifier[]{ avgRatingExpr.desc().nullsLast(), hotel.id.asc() };
        }
    }
}