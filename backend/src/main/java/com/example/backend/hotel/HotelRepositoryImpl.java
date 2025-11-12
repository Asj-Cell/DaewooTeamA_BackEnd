package com.example.backend.hotel;

import com.example.backend.hotel.hotelfilters.dto.HotelFilterRequestDto;
import com.example.backend.hotel.hotelfilters.dto.HotelFiltersDto;
import com.example.backend.hotel.hotelfilters.dto.QHotelFiltersDto;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.*; // dsl 패키지 import
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery; // JPQLQuery import 추가 (isRoomAvailableSubquery 반환 타입)
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
// import org.slf4j.Logger; // 로깅 사용시
// import org.slf4j.LoggerFactory; // 로깅 사용시
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

// QClass static import
import static com.example.backend.Reservation.QReservation.reservation;
import static com.example.backend.room.entity.QRoom.room;
import static com.example.backend.amenities.entity.QAmenities.amenities;
import static com.example.backend.freebies.entity.QFreebies.freebies;
import static com.example.backend.hotel.entity.QHotel.hotel;
// import static com.example.backend.hotel.entity.QHotelImage.hotelImage; // 사용 안 함
import static com.example.backend.review.entity.QReview.review;
import static com.example.backend.favorites.entity.QFavorites.favorites;

// 서브쿼리용 별칭 QClass
import com.example.backend.room.entity.QRoom;
import com.example.backend.Reservation.QReservation;


@Repository
@RequiredArgsConstructor
public class HotelRepositoryImpl implements HotelRepositoryCustom {

    // private static final Logger log = LoggerFactory.getLogger(HotelRepositoryImpl.class); // 로깅 사용시

    // 서브쿼리용 별칭 인스턴스
    private static final QRoom subRoomForMinPrice = new QRoom("subRoomForMinPrice");
    // private static final QReservation subResForMinPrice = new QReservation("subResForMinPrice"); // isRoomAvailableSubquery 내부 생성
    private static final QRoom subRoomForAvail = new QRoom("subRoomForAvail");
    // private static final QReservation subResForAvail = new QReservation("subResForAvail"); // isRoomAvailableSubquery 내부 생성
    private static final QRoom subRoomForGuest = new QRoom("subRoomForGuest");
    private static final QRoom subRoomForPriceRange = new QRoom("subRoomForPriceRange");
    private static final QRoom subRoomForAvailPrice = new QRoom("subRoomForAvailPrice");
    // private static final QReservation subResForAvailPrice = new QReservation("subResForAvailPrice"); // isRoomAvailableSubquery 내부 생성


    private final JPAQueryFactory queryFactory;

    @Override
    public Page<HotelFiltersDto> findHotelsByFilters(HotelFilterRequestDto filter, Pageable pageable, Long loginUserId) {

        final Long userId = loginUserId != null ? loginUserId : -1L;

        try {
            // 리뷰 평균 계산 (호텔 그룹별)
            NumberExpression<Double> avgRating = review.userRatingScore.avg().coalesce(0.0);

            // 리뷰 개수 계산 (호텔 그룹별)
            NumberExpression<Long> reviewCount = review.countDistinct(); // review 중복 방지

            // 최소 가격 계산 (호텔 그룹별, 예약 가능한 방만 대상)
            // 📌 중요: checkInDate와 checkOutDate가 있으면, 해당 기간에 예약된 방은 제외하고 최저가를 계산합니다.
            // 예: 30만원 방이 예약되어 있으면 → 35만원 방이 최저가로 표시됩니다.
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
                            amenitiesCountExpr,    // 5. amenitiesCount
                            minAvailablePriceExpr, // 6. minAvailablePriceExpr
                            avgRating,             // 7. avgRating
                            Expressions.constant(Collections.<String>emptyList()), // 8. imageUrls (서비스에서 채움)
                            isFavoriteExpr,        // 9. isFavorite
                            reviewCount            // 10. reviewCount
                    ))
                    .from(hotel)
                    .leftJoin(hotel.reviews, review)
                    .leftJoin(hotel.freebies, freebies)
                    .leftJoin(hotel.amenities, amenities)
                    .leftJoin(hotel.rooms, room) // 메인 room 조인 (WHERE 조건용)
                    // Group by Non-aggregated selected columns
                    .groupBy(hotel.id, hotel.name, hotel.address, hotel.grade); // amenitiesCountExpr, minAvailablePriceExpr, isFavoriteExpr는 집계 또는 서브쿼리 결과이므로 groupBy 불필요

            // 필터 조건 적용 (where 절) - 호텔 자체를 필터링
            BooleanExpression conditions = createConditions(filter);
            if (conditions != null) {
                query.where(conditions);
            }

            // 평점 필터는 'having' 절에서 처리 (avgRating 계산 후)
            if (filter.getMinAvgRating() != null) {
                query.having(avgRating.goe(filter.getMinAvgRating().doubleValue()));
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

            // 카운트 쿼리
            JPAQuery<Long> countQuery = queryFactory
                    .select(hotel.countDistinct())
                    .from(hotel)
                    .leftJoin(hotel.rooms, room)
                    .leftJoin(hotel.reviews, review)
                    .leftJoin(hotel.freebies, freebies)
                    .leftJoin(hotel.amenities, amenities);

            if (conditions != null) {
                countQuery.where(conditions);
            }
            if (filter.getMinAvgRating() != null) {
                countQuery.where(avgRatingGoeSubquery(filter.getMinAvgRating()));
            }

            return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);

        } catch (Exception e) {
            // log.error("Error querying hotels with filter: {}, pageable: {}", filter, pageable, e); // 상세 로깅
            throw new RuntimeException("Error while querying hotels: " + e.getMessage(), e); // 원인 예외 포함
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

        // 가격 범위 필터
        if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
            if (filter.getCheckInDate() != null && filter.getCheckOutDate() != null) {
                conditions = and(conditions, hotelHasAvailableRoomInPriceRange(
                        filter.getCheckInDate(), filter.getCheckOutDate(),
                        filter.getMinPrice(), filter.getMaxPrice()
                ));
            } else {
                conditions = and(conditions, hotelHasRoomInPriceRange(filter.getMinPrice(), filter.getMaxPrice()));
            }
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

    // [FIXED] 에일리어스 생성 시 하이픈 제거
    /**
     * 특정 방이 주어진 체크인-체크아웃 기간에 예약되어 있는지 확인하는 서브쿼리
     * 
     * 📌 날짜 겹침 로직:
     * - 예약의 체크인 < 요청 체크아웃 AND 예약의 체크아웃 > 요청 체크인
     * - 이 조건을 만족하면 예약이 "겹친다"고 판단
     * 
     * 예시:
     * - 기존 예약: 10/10 ~ 10/15
     * - 요청: 10/12 ~ 10/14 → 겹침 ✅ (예약됨)
     * - 요청: 10/01 ~ 10/09 → 겹침 없음 ❌ (예약 가능)
     * - 요청: 10/16 ~ 10/20 → 겹침 없음 ❌ (예약 가능)
     * 
     * @param roomAlias 체크할 방의 별칭
     * @param checkIn 요청 체크인 날짜
     * @param checkOut 요청 체크아웃 날짜
     * @return 예약이 존재하면 1을 반환하는 서브쿼리 (notExists()와 함께 사용)
     */
    private JPQLQuery<Integer> isRoomAvailableSubquery(QRoom roomAlias, LocalDate checkIn, LocalDate checkOut) {
        // 날짜 포함 대신 roomAlias 문자열 기반으로 고유 에일리어스 생성 (하이픈 제거)
        String resAliasName = roomAlias.toString().replace(".", "_") + "_res";
        QReservation resAlias = new QReservation(resAliasName);
        return JPAExpressions.selectOne()
                .from(resAlias)
                .where(resAlias.room.eq(roomAlias),
                        resAlias.checkinDate.lt(checkOut),    // 예약 체크인 < 요청 체크아웃
                        resAlias.checkoutDate.gt(checkIn)      // 예약 체크아웃 > 요청 체크인
                );
    }

    // 호텔이 특정 가격 범위의 방을 가지고 있는지 (날짜 무관)
    private BooleanExpression hotelHasRoomInPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice == null && maxPrice == null) return Expressions.asBoolean(true).isTrue();
        BooleanExpression priceCondition = null;
        if (minPrice != null) { priceCondition = and(priceCondition, subRoomForPriceRange.price.goe(minPrice)); }
        if (maxPrice != null) { priceCondition = and(priceCondition, subRoomForPriceRange.price.loe(maxPrice)); }

        return JPAExpressions.selectOne()
                .from(subRoomForPriceRange)
                .where(subRoomForPriceRange.hotel.id.eq(hotel.id), priceCondition)
                .exists();
    }

    // 호텔이 특정 가격 범위의 '예약 가능한' 방을 가지고 있는지
    private BooleanExpression hotelHasAvailableRoomInPriceRange(LocalDate checkIn, LocalDate checkOut, BigDecimal minPrice, BigDecimal maxPrice) {
        if (checkIn == null || checkOut == null) return hotelHasRoomInPriceRange(minPrice, maxPrice);
        if (minPrice == null && maxPrice == null) return hotelHasAvailableRoom(checkIn, checkOut);

        BooleanExpression priceCondition = null;
        if (minPrice != null) { priceCondition = and(priceCondition, subRoomForAvailPrice.price.goe(minPrice)); }
        if (maxPrice != null) { priceCondition = and(priceCondition, subRoomForAvailPrice.price.loe(maxPrice)); }
        // priceCondition이 null이 될 수 없음 (위에서 null 체크함)

        return JPAExpressions.selectOne()
                .from(subRoomForAvailPrice)
                .where(
                        subRoomForAvailPrice.hotel.id.eq(hotel.id),
                        priceCondition, // priceCondition은 여기서 null이 아님
                        isRoomAvailableSubquery(subRoomForAvailPrice, checkIn, checkOut).notExists()
                ).exists();
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

    // 카운트 쿼리용 평점 필터
    private BooleanExpression avgRatingGoeSubquery(Integer minRating) {
        if (minRating == null) return null;
        return JPAExpressions.select(review.userRatingScore.avg().coalesce(0.0))
                .from(review)
                .where(review.hotel.id.eq(hotel.id))
                .goe(minRating.doubleValue());
    }

    // --- 편의시설 / 무료혜택 필터 ---
    private BooleanExpression hasBreakfast(Boolean value) { return value != null && value ? freebies.breakfastIncluded.isTrue() : null; }
    private BooleanExpression hasFreeParking(Boolean value) { return value != null && value ? freebies.freeParking.isTrue() : null; }
    private BooleanExpression hasFreeWifi(Boolean value) { return value != null && value ? freebies.freeWifi.isTrue() : null; }
    private BooleanExpression hasAirportShuttle(Boolean value) { return value != null && value ? freebies.airportShuttlebus.isTrue() : null; }
    private BooleanExpression hasFreeCancellation(Boolean value) { return value != null && value ? freebies.freeCancellation.isTrue() : null; }
    private BooleanExpression hasFrontDesk24(Boolean value) { return value != null && value ? amenities.frontDesk24.isTrue() : null; }
    private BooleanExpression hasAirConditioner(Boolean value) { return value != null && value ? amenities.airConditioner.isTrue() : null; }
    private BooleanExpression hasFitnessCenter(Boolean value) { return value != null && value ? amenities.fitnessCenter.isTrue() : null; }
    private BooleanExpression hasPool(Boolean value) { return value != null && value ? amenities.indoorPool.isTrue().or(amenities.outdoorPool.isTrue()) : null; }

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
            default: // Default or empty sortBy
                return new OrderSpecifier[]{ avgRatingExpr.desc().nullsLast(), hotel.id.asc() };
        }
    }
}

