
-- 사용자 테이블
CREATE TABLE IF NOT EXISTS  `user` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `user_name` varchar(100) NOT NULL,
                        `email` varchar(100) NOT NULL,
                        `password` varchar(100) DEFAULT NULL,
                        `phone_number` varchar(100) DEFAULT NULL,
                        `provider` varchar(255) DEFAULT NULL,
                        `provider_id` varchar(255) DEFAULT NULL,
                        `address` varchar(100) DEFAULT NULL,
                        `birth_date` date DEFAULT NULL,
                        `image_url` varchar(255) DEFAULT NULL,
                        `background_image_url` varchar(255) DEFAULT NULL,
                        `enabled` bit(1) NOT NULL DEFAULT b'1',
                        `reset_token` varchar(255) DEFAULT NULL,
                        `reset_token_expiry` datetime(6) DEFAULT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UK_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 도시 테이블
CREATE TABLE IF NOT EXISTS  `city` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `city_name` varchar(20) NOT NULL,
                        `country` varchar(20) NOT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 호텔 테이블
CREATE TABLE IF NOT EXISTS `hotel` (
                         `id` bigint(20) NOT NULL AUTO_INCREMENT,
                         `name` varchar(200) NOT NULL,
                         `grade` int(11) NOT NULL,
                         `overview` varchar(100) NOT NULL,
                         `latitude` double NOT NULL,
                         `longitude` double NOT NULL,
                         `address` varchar(100) NOT NULL,
                         `checkin_time` time(6) NOT NULL,
                         `checkout_time` time(6) NOT NULL,
                         `city_id` bigint(20) NOT NULL,
                         PRIMARY KEY (`id`),
                         KEY `FK_hotel_to_city` (`city_id`),
                         CONSTRAINT `FK_hotel_to_city` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 무료 서비스 테이블
CREATE TABLE IF NOT EXISTS `freebies` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `breakfast_included` bit(1) DEFAULT NULL,
                            `free_parking` bit(1) DEFAULT NULL,
                            `free_wifi` bit(1) DEFAULT NULL,
                            `airport_shuttlebus` bit(1) DEFAULT NULL,
                            `free_cancellation` bit(1) DEFAULT NULL,
                            `hotel_id` bigint(20) DEFAULT NULL,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `UK_freebies_hotel_id` (`hotel_id`),
                            CONSTRAINT `FK_freebies_to_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 편의시설 테이블
CREATE TABLE IF NOT EXISTS `amenities` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `front_desk24` bit(1) DEFAULT NULL,
                             `outdoor_pool` bit(1) DEFAULT NULL,
                             `indoor_pool` bit(1) DEFAULT NULL,
                             `spa_wellness_center` bit(1) DEFAULT NULL,
                             `restaurant` bit(1) DEFAULT NULL,
                             `roomservice` bit(1) DEFAULT NULL,
                             `fitness_center` bit(1) DEFAULT NULL,
                             `bar_lounge` bit(1) DEFAULT NULL,
                             `tea_coffee_machine` bit(1) DEFAULT NULL,
                             `airconditioning` bit(1) DEFAULT NULL,
                             `hotel_id` bigint(20) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             UNIQUE KEY `UK_amenities_hotel_id` (`hotel_id`),
                             CONSTRAINT `FK_amenities_to_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 객실 테이블
CREATE TABLE IF NOT EXISTS `room` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `room_number` varchar(10) NOT NULL,
                        `price` decimal(38,2) NOT NULL,
                        `name` varchar(20) NOT NULL,
                        `view` varchar(20) DEFAULT NULL,
                        `bed` varchar(50) DEFAULT NULL,
                        `max_guests` int(11) NOT NULL,
                        `hotel_id` bigint(20) NOT NULL,
                        PRIMARY KEY (`id`),
                        KEY `FK_room_to_hotel` (`hotel_id`),
                        CONSTRAINT `FK_room_to_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 객실 이미지 테이블
CREATE TABLE IF NOT EXISTS `room_img` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `image_url` varchar(255) NOT NULL,
                            `size` int(11) NOT NULL,
                            `room_id` bigint(20) NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK_room_img_room` (`room_id`),
                            CONSTRAINT `FK_room_img_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 호텔 이미지 테이블
CREATE TABLE IF NOT EXISTS `hotel_image` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `image_url` varchar(255) NOT NULL,
                               `sequence` int(11) NOT NULL,
                               `size` int(11) NOT NULL,
                               `hotel_id` bigint(20) NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FK_hotel_image_to_hotel` (`hotel_id`),
                               CONSTRAINT `FK_hotel_image_to_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 여행 패키지 테이블
CREATE TABLE IF NOT EXISTS `travel_package` (
                                  `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                  `title` varchar(50) NOT NULL,
                                  `description` varchar(300) NOT NULL,
                                  `price` decimal(38,2) NOT NULL,
                                  `st_date` date NOT NULL,
                                  `end_date` date NOT NULL,
                                  `city_id` bigint(20) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  KEY `FK_travel_package_to_city` (`city_id`),
                                  CONSTRAINT `FK_travel_package_to_city` FOREIGN KEY (`city_id`) REFERENCES `city` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 패키지 이미지 테이블
CREATE TABLE IF NOT EXISTS `package_image` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `image_url` varchar(255) NOT NULL,
                                 `package_id` bigint(20) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `FK_package_image_to_package` (`package_id`),
                                 CONSTRAINT `FK_package_image_to_package` FOREIGN KEY (`package_id`) REFERENCES `travel_package` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 찜 목록 테이블
CREATE TABLE IF NOT EXISTS `favorites` (
                             `id` bigint(20) NOT NULL AUTO_INCREMENT,
                             `hotel_id` bigint(20) DEFAULT NULL,
                             `user_id` bigint(20) DEFAULT NULL,
                             PRIMARY KEY (`id`),
                             KEY `FK_favorites_to_hotel` (`hotel_id`),
                             KEY `FK_favorites_to_user` (`user_id`),
                             CONSTRAINT `FK_favorites_to_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`),
                             CONSTRAINT `FK_favorites_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 리뷰 테이블
CREATE TABLE IF NOT EXISTS `review` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `content` varchar(255) NOT NULL,
                          `user_rating_score` double NOT NULL,
                          `report_yn` bit(1) NOT NULL DEFAULT b'0',
                          `hotel_id` bigint(20) NOT NULL,
                          `user_id` bigint(20) NOT NULL,
                          PRIMARY KEY (`id`),
                          KEY `FK_review_to_hotel` (`hotel_id`),
                          KEY `FK_review_to_user` (`user_id`),
                          CONSTRAINT `FK_review_to_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`),
                          CONSTRAINT `FK_review_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--  예약 테이블: is_deleted 컬럼 추가
CREATE TABLE IF NOT EXISTS `reservation` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `check_in_date` date NOT NULL,
                               `check_out_date` date NOT NULL,
                               `discount` decimal(10,2) DEFAULT NULL,
                               `taxes` decimal(10,2) DEFAULT NULL,
                               `service_fee` decimal(10,2) DEFAULT NULL,
                               `total_price` decimal(10,2) NOT NULL,
                               `room_id` bigint(20) NOT NULL,
                               `user_id` bigint(20) NOT NULL,
                               `is_deleted` BOOLEAN NOT NULL DEFAULT FALSE, --  소프트 삭제를 위한 컬럼 추가
                               PRIMARY KEY (`id`),
                               KEY `FK_reservation_to_room` (`room_id`),
                               KEY `FK_reservation_to_user` (`user_id`),
                               CONSTRAINT `FK_reservation_to_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
                               CONSTRAINT `FK_reservation_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 쿠폰 테이블 (신규 추가)
CREATE TABLE IF NOT EXISTS `coupon` (
                          `id` bigint(20) NOT NULL AUTO_INCREMENT,
                          `name` varchar(255) NOT NULL,
                          `discount_amount` decimal(10, 2) NOT NULL,
                          `expiry_date` date NOT NULL,
                          `is_used` bit(1) NOT NULL DEFAULT b'0',
                          `user_id` bigint(20) NOT NULL,
                          `reservation_id` bigint(20) DEFAULT NULL,
                          PRIMARY KEY (`id`),
                          UNIQUE KEY `UK_coupon_reservation_id` (`reservation_id`),
                          KEY `FK_coupon_to_user` (`user_id`),
                          CONSTRAINT `FK_coupon_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                          CONSTRAINT `FK_coupon_to_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 결제 수단 테이블 (변경 없음)
CREATE TABLE IF NOT EXISTS `payment` (
                           `id` bigint(20) NOT NULL AUTO_INCREMENT,
                           `payment_name` varchar(100) DEFAULT NULL,
                           `payment_number` varchar(50) DEFAULT NULL,
                           `expiration_date` date DEFAULT NULL,
                           `cvc` varchar(10) DEFAULT NULL,
                           `card_user` varchar(100) DEFAULT NULL,
                           `country` varchar(100) DEFAULT NULL,
                           `registration_date` datetime(6) DEFAULT NULL,
                           `user_id` bigint(20) NOT NULL,
                           `is_deleted` bit(1) DEFAULT 0,
                           PRIMARY KEY (`id`),
                           KEY `FK_payment_to_user` (`user_id`),
                           CONSTRAINT `FK_payment_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--  결제 내역 테이블: payment_key 추가, payment_id 제거
CREATE TABLE IF NOT EXISTS `pay` (
                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                       `payment_key` VARCHAR(255) UNIQUE, --  토스페이먼츠 키 저장을 위한 컬럼 추가
                       `payment_gateway` VARCHAR(20),
                       `redate` DATETIME(6),
                       `price` DECIMAL(10, 2),
    -- `payment_id` BIGINT, --  더 이상 사용하지 않으므로 제거
                       `user_id` BIGINT,
                       `reservation_id` BIGINT,
                       PRIMARY KEY (`id`),
    -- CONSTRAINT `FK_pay_to_payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`), --  관련 제약조건 제거
                       CONSTRAINT `FK_pay_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                       CONSTRAINT `FK_pay_to_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

