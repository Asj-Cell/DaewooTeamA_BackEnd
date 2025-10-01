-- 1. 테이블 초기화 (참조 관계의 역순으로 삭제)
DROP TABLE IF EXISTS `pay`;
DROP TABLE IF EXISTS `payment`;
DROP TABLE IF EXISTS `review`;
DROP TABLE IF EXISTS `favorites`;
DROP TABLE IF EXISTS `reservation`;
DROP TABLE IF EXISTS `package_image`;
DROP TABLE IF EXISTS `travel_package`;
DROP TABLE IF EXISTS `hotel_image`;
DROP TABLE IF EXISTS `room_img`;
DROP TABLE IF EXISTS `room`;
DROP TABLE IF EXISTS `amenities`;
DROP TABLE IF EXISTS `freebies`;
DROP TABLE IF EXISTS `hotel`;
DROP TABLE IF EXISTS `city`;
DROP TABLE IF EXISTS `user`;


-- 2. 테이블 생성 (DDL)

-- 사용자 테이블
CREATE TABLE `user` (
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
CREATE TABLE `city` (
                        `id` bigint(20) NOT NULL AUTO_INCREMENT,
                        `city_name` varchar(20) NOT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 호텔 테이블
CREATE TABLE `hotel` (
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
CREATE TABLE `freebies` (
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
CREATE TABLE `amenities` (
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
CREATE TABLE `room` (
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
CREATE TABLE `room_img` (
                            `id` bigint(20) NOT NULL AUTO_INCREMENT,
                            `image_url` varchar(255) NOT NULL,
                            `size` int(11) NOT NULL,
                            `room_id` bigint(20) NOT NULL,
                            PRIMARY KEY (`id`),
                            KEY `FK_room_img_room` (`room_id`),
                            CONSTRAINT `FK_room_img_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 호텔 이미지 테이블
CREATE TABLE `hotel_image` (
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
CREATE TABLE `travel_package` (
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
CREATE TABLE `package_image` (
                                 `id` bigint(20) NOT NULL AUTO_INCREMENT,
                                 `image_url` varchar(255) NOT NULL,
                                 `package_id` bigint(20) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 KEY `FK_package_image_to_package` (`package_id`),
                                 CONSTRAINT `FK_package_image_to_package` FOREIGN KEY (`package_id`) REFERENCES `travel_package` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 찜 목록 테이블
CREATE TABLE `favorites` (
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
CREATE TABLE `review` (
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

-- 예약 테이블
CREATE TABLE `reservation` (
                               `id` bigint(20) NOT NULL AUTO_INCREMENT,
                               `check_in_date` date NOT NULL,
                               `check_out_date` date NOT NULL,
                               `discount` decimal(10,2) DEFAULT NULL,
                               `taxes` decimal(10,2) DEFAULT NULL,
                               `total_price` decimal(10,2) NOT NULL,
                               `room_id` bigint(20) NOT NULL,
                               `user_id` bigint(20) NOT NULL,
                               PRIMARY KEY (`id`),
                               KEY `FK_reservation_to_room` (`room_id`),
                               KEY `FK_reservation_to_user` (`user_id`),
                               CONSTRAINT `FK_reservation_to_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`),
                               CONSTRAINT `FK_reservation_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 결제 수단 테이블
CREATE TABLE `payment` (
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

-- 결제 내역 테이블
CREATE TABLE `pay` (
                       `id` BIGINT NOT NULL AUTO_INCREMENT,
                       `payment_gateway` VARCHAR(20),
                       `redate` DATETIME(6),
                       `price` DECIMAL(10, 2),
                       `payment_id` BIGINT,
                       `user_id` BIGINT,
                       `reservation_id` BIGINT,
                       PRIMARY KEY (`id`),
                       CONSTRAINT `FK_pay_to_payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`),
                       CONSTRAINT `FK_pay_to_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                       CONSTRAINT `FK_pay_to_reservation` FOREIGN KEY (`reservation_id`) REFERENCES `reservation` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 3. 데이터 삽입 (DML)

-- 사용자 데이터
INSERT INTO `user` (`id`, `user_name`, `email`, `password`, `phone_number`, `address`, `birth_date`, `image_url`, `background_image_url`)
VALUES
    (1, '김민준', 'minjun.kim@example.com', '$2a$10$E/a3J5..L27GjQW3.p2yC.i2u.j5.a1b.c1d.e1f.g1h', '010-1234-5678', '서울시 강남구', '1990-01-15', '/uploads/profile1.png', '/uploads/bg1.png'),
    (2, '이서연', 'seoyeon.lee@example.com', '$2a$10$E/a3J5..L27GjQW3.p2yC.i2u.j5.a1b.c1d.e1f.g1h', '010-2345-6789', '부산시 해운대구', '1992-05-20', '/uploads/profile2.png', '/uploads/bg2.png'),
    (3, '박준호', 'junho.park@example.com', '$2a$10$E/a3J5..L27GjQW3.p2yC.i2u.j5.a1b.c1d.e1f.g1h', '010-3456-7890', '인천시 연수구', '1988-08-10', '/uploads/profile3.png', '/uploads/bg3.png'),
    (4, '최지우', 'jiwoo.choi@example.com', '$2a$10$E/a3J5..L27GjQW3.p2yC.i2u.j5.a1b.c1d.e1f.g1h', '010-4567-8901', '대구시 수성구', '1995-11-25', '/uploads/profile4.png', '/uploads/bg4.png'),
    (5, '정유진', 'yujin.jung@example.com', '$2a$10$E/a3J5..L27GjQW3.p2yC.i2u.j5.a1b.c1d.e1f.g1h', '010-5678-9012', '광주시 서구', '1998-03-30', '/uploads/profile5.png', '/uploads/bg5.png');

-- 도시 데이터
INSERT INTO `city` (`id`, `city_name`) VALUES
                                           (1, '서울'), (2, '부산'), (3, '제주'), (4, '인천'), (5, '경주'),
                                           (6, '파리'), (7, '런던'), (8, '도쿄'), (9, '뉴욕'), (10, '방콕');

-- 호텔 데이터
INSERT INTO `hotel` (`id`, `name`, `grade`, `overview`, `latitude`, `longitude`, `address`, `checkin_time`, `checkout_time`, `city_id`) VALUES
                                                                                                                                            (1, '신라호텔', 5, '최고급 서비스와 시설', 37.5558, 127.0053, '서울시 중구 동호로 249', '15:00:00', '12:00:00', 1),
                                                                                                                                            (2, '파라다이스 호텔 부산', 5, '해운대 해변의 럭셔리 호텔', 35.1598, 129.1603, '부산시 해운대구 해운대해변로 296', '15:00:00', '11:00:00', 2),
                                                                                                                                            (3, '롯데호텔 제주', 5, '중문관광단지 리조트형 호텔', 33.2476, 126.4107, '제주특별자치도 서귀포시 중문관광로72번길 35', '14:00:00', '11:00:00', 3),
                                                                                                                                            (4, '경원재 앰배서더 인천', 5, '송도 센트럴파크의 한옥 호텔', 37.3947, 126.6384, '인천시 연수구 테크노파크로 200', '15:00:00', '11:00:00', 4),
                                                                                                                                            (5, '힐튼 경주', 5, '보문호수가 보이는 호텔', 35.8400, 129.2818, '경북 경주시 보문로 484-7', '15:00:00', '11:00:00', 5),
                                                                                                                                            (6, '리츠 파리', 5, '파리 중심부의 상징적인 호텔', 48.8679, 2.3275, '15 Place Vendôme, 75001 Paris, France', '15:00:00', '12:00:00', 6),
                                                                                                                                            (7, '더 사보이', 5, '런던의 역사와 전통을 자랑하는 호텔', 51.5100, -0.1207, 'Strand, London WC2R 0EZ, UK', '15:00:00', '12:00:00', 7),
                                                                                                                                            (8, '파크 하얏트 도쿄', 5, '신주쿠의 전망을 즐길 수 있는 호텔', 35.6850, 139.6900, '3-7-1-2 Nishi-Shinjuku, Shinjuku-ku, Tokyo, Japan', '15:00:00', '12:00:00', 8);

-- 호텔별 무료 서비스 및 편의시설 데이터
INSERT INTO `freebies` (`id`, `breakfast_included`, `free_parking`, `free_wifi`, `airport_shuttlebus`, `free_cancellation`, `hotel_id`) VALUES
                                                                                                                                            (1, 1, 1, 1, 1, 1, 1), (2, 0, 1, 1, 1, 1, 2), (3, 1, 1, 1, 1, 0, 3), (4, 1, 1, 1, 0, 1, 4),
                                                                                                                                            (5, 0, 1, 1, 0, 1, 5), (6, 1, 0, 1, 1, 0, 6), (7, 1, 0, 1, 1, 1, 7), (8, 1, 1, 1, 0, 1, 8);

INSERT INTO `amenities` (`id`, `front_desk24`, `outdoor_pool`, `indoor_pool`, `spa_wellness_center`, `restaurant`, `roomservice`, `fitness_center`, `bar_lounge`, `tea_coffee_machine`, `airconditioning`, `hotel_id`) VALUES
                                                                                                                                                                                                                           (1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1), (2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 2), (3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 3), (4, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1, 4),
                                                                                                                                                                                                                           (5, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 5), (6, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 6), (7, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 7), (8, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 8);

-- 객실 데이터 (id를 1~32까지 순차적으로 정렬)
INSERT INTO `room` (`id`, `room_number`, `price`, `name`, `view`, `bed`, `max_guests`, `hotel_id`) VALUES
-- 호텔 1 (신라호텔) : id 1-4
(1, '101', 250000.00, '스탠다드', '시티 뷰', '더블 베드', 2, 1),
(2, '102', 350000.00, '디럭스', '시티 뷰', '킹 베드', 2, 1),
(3, '103', 450000.00, '스위트', '남산 뷰', '킹 베드', 3, 1),
(4, '104', 550000.00, '프리미어 스위트', '남산 뷰', '킹 베드 2개', 4, 1),
-- 호텔 2 (파라다이스 호텔 부산) : id 5-8
(5, '201', 300000.00, '스탠다드 오션', '오션 뷰', '더블 베드', 2, 2),
(6, '202', 400000.00, '디럭스 오션', '오션 뷰', '킹 베드', 2, 2),
(7, '203', 500000.00, '오션 스위트', '오션 프론트 뷰', '킹 베드', 3, 2),
(8, '204', 600000.00, '패밀리 오션', '오션 프론트 뷰', '더블 베드 2개', 4, 2),
-- 호텔 3 (롯데호텔 제주) : id 9-12
(9, '301', 220000.00, '슈페리어', '마운틴 뷰', '퀸 베드', 2, 3),
(10, '302', 300000.00, '디럭스', '가든 뷰', '킹 베드', 2, 3),
(11, '303', 380000.00, '풀빌라', '프라이빗 풀 뷰', '킹 베드', 2, 3),
(12, '304', 450000.00, '가든 스위트', '가든 뷰', '킹 베드', 4, 3),
-- 호텔 4 (경원재 앰배서더 인천) : id 13-16
(13, '401', 180000.00, '게스트 룸', '시티 뷰', '더블 베드', 2, 4),
(14, '402', 280000.00, '스위트', '파크 뷰', '킹 베드', 3, 4),
(15, '403', 350000.00, '디럭스 스위트', '파크 뷰', '킹 베드', 3, 4),
(16, '404', 420000.00, '로얄 스위트', '파크 프론트 뷰', '킹 베드', 4, 4),
-- 호텔 5 (힐튼 경주) : id 17-20
(17, '501', 150000.00, '스탠다드', '가든 뷰', '싱글 베드', 1, 5),
(18, '502', 200000.00, '스탠다드 더블', '가든 뷰', '더블 베드', 2, 5),
(19, '503', 250000.00, '디럭스', '호수 뷰', '킹 베드', 2, 5),
(20, '504', 300000.00, '패밀리룸', '호수 뷰', '더블 베드 2개', 4, 5),
-- 호텔 6 (리츠 파리) : id 21-24
(21, '601', 800000.00, '슈페리어 룸', '가든 뷰', '퀸 베드', 2, 6),
(22, '602', 950000.00, '이그제큐티브 룸', '가든 뷰', '킹 베드', 2, 6),
(23, '603', 1200000.00, '디럭스 스위트', '에펠탑 뷰', '킹 베드', 3, 6),
(24, '604', 1500000.00, '프레스티지 스위트', '에펠탑 뷰', '킹 베드', 3, 6),
-- 호텔 7 (더 사보이) : id 25-28
(25, '701', 750000.00, '슈페리어 퀸', '시티 뷰', '퀸 베드', 2, 7),
(26, '702', 900000.00, '디럭스 킹', '템즈강 뷰', '킹 베드', 2, 7),
(27, '703', 1100000.00, '원 베드룸 스위트', '템즈강 뷰', '킹 베드', 3, 7),
(28, '704', 1300000.00, '리버뷰 스위트', '템즈강 프론트 뷰', '킹 베드', 3, 7),
-- 호텔 8 (파크 하얏트 도쿄) : id 29-32
(29, '801', 650000.00, '파크 뷰 킹', '신주쿠 파크 뷰', '킹 베드', 2, 8),
(30, '802', 850000.00, '파크 스위트', '신주쿠 파크 뷰', '킹 베드', 3, 8),
(31, '803', 1000000.00, '디플로매트 스위트', '후지산 뷰', '킹 베드', 3, 8),
(32, '804', 1200000.00, '프레지덴셜 스위트', '파노라마 뷰', '킹 베드', 4, 8);


-- 객실 이미지 데이터 (room_id를 새로 정렬된 room.id에 맞게 수정)
INSERT INTO `room_img` (`id`, `image_url`, `size`, `room_id`) VALUES
-- 호텔 1 이미지 (room_id: 1-4)
(1, '/images/hotel1/room1.png', 512, 1),
(2, '/images/hotel1/room2.png', 512, 2),
(3, '/images/hotel1/room3.png', 512, 3),
(4, '/images/hotel1/room4.png', 512, 4),
-- 호텔 2 이미지 (room_id: 5-8)
(5, '/images/hotel2/room1.png', 512, 5),
(6, '/images/hotel2/room2.png', 512, 6),
(7, '/images/hotel2/room3.png', 512, 7),
(8, '/images/hotel2/room4.png', 512, 8),
-- 호텔 3 이미지 (room_id: 9-12)
(9, '/images/hotel3/room1.png', 512, 9),
(10, '/images/hotel3/room2.png', 512, 10),
(11, '/images/hotel3/room3.png', 512, 11),
(12, '/images/hotel3/room4.png', 512, 12),
-- 호텔 4 이미지 (room_id: 13-16)
(13, '/images/hotel4/room1.png', 512, 13),
(14, '/images/hotel4/room2.png', 512, 14),
(15, '/images/hotel4/room3.png', 512, 15),
(16, '/images/hotel4/room4.png', 512, 16),
-- 호텔 5 이미지 (room_id: 17-20)
(17, '/images/hotel5/room1.png', 512, 17),
(18, '/images/hotel5/room2.png', 512, 18),
(19, '/images/hotel5/room3.png', 512, 19),
(20, '/images/hotel5/room4.png', 512, 20),
-- 호텔 6 이미지 (room_id: 21-24)
(21, '/images/hotel6/room1.png', 512, 21),
(22, '/images/hotel6/room2.png', 512, 22),
(23, '/images/hotel6/room3.png', 512, 23),
(24, '/images/hotel6/room4.png', 512, 24),
-- 호텔 7 이미지 (room_id: 25-28)
(25, '/images/hotel7/room1.png', 512, 25),
(26, '/images/hotel7/room2.png', 512, 26),
(27, '/images/hotel7/room3.png', 512, 27),
(28, '/images/hotel7/room4.png', 512, 28),
-- 호텔 8 이미지 (room_id: 29-32)
(29, '/images/hotel8/room1.png', 512, 29),
(30, '/images/hotel8/room2.png', 512, 30),
(31, '/images/hotel8/room3.png', 512, 31),
(32, '/images/hotel8/room4.png', 512, 32);


-- 호텔 이미지 데이터 (요청하신 파일 경로 규칙으로 수정)
INSERT INTO `hotel_image` (`id`, `image_url`, `sequence`, `size`, `hotel_id`) VALUES
                                                                                  (1, '/images/hotel1/hotel1_main.png', 1, 1024, 1), (2, '/images/hotel1/hotel1_sub.png', 2, 512, 1),
                                                                                  (3, '/images/hotel2/hotel2_main.png', 1, 1024, 2), (4, '/images/hotel2/hotel2_sub.png', 2, 512, 2),
                                                                                  (5, '/images/hotel3/hotel3_main.png', 1, 1024, 3), (6, '/images/hotel3/hotel3_sub.png', 2, 512, 3),
                                                                                  (7, '/images/hotel4/hotel4_main.png', 1, 1024, 4), (8, '/images/hotel4/hotel4_sub.png', 2, 512, 4),
                                                                                  (9, '/images/hotel5/hotel5_main.png', 1, 1024, 5), (10, '/images/hotel5/hotel5_sub.png', 2, 512, 5),
                                                                                  (11, '/images/hotel6/hotel6_main.png', 1, 1024, 6), (12, '/images/hotel6/hotel6_sub.png', 2, 512, 6),
                                                                                  (13, '/images/hotel7/hotel7_main.png', 1, 1024, 7), (14, '/images/hotel7/hotel7_sub.png', 2, 512, 7),
                                                                                  (15, '/images/hotel8/hotel8_main.png', 1, 1024, 8), (16, '/images/hotel8/hotel8_sub.png', 2, 512, 8);
-- 여행 패키지 데이터
INSERT INTO `travel_package` (`id`, `title`, `description`, `price`, `st_date`, `end_date`, `city_id`) VALUES
                                                                                                           (1, '서울 2박 3일 시티 투어', '서울의 주요 관광지인 경복궁, 명동, N서울타워를 둘러보는 알찬 패키지입니다.', 350000.00, '2025-10-10', '2025-10-12', 1),
                                                                                                           (2, '부산 3박 4일 미식 여행', '부산의 명물인 돼지국밥, 씨앗호떡, 해산물 맛집을 탐방하는 미식 여행 패키지입니다.', 450000.00, '2025-11-15', '2025-11-18', 2),
                                                                                                           (3, '제주 4박 5일 힐링 여행', '한라산 등반, 올레길 산책, 아름다운 해변에서 즐기는 여유로운 힐링 여행입니다.', 550000.00, '2025-12-20', '2025-12-24', 3),
                                                                                                           (4, '파리 4박 5일 예술 기행', '루브르 박물관, 오르세 미술관, 에펠탑 등 파리의 예술과 낭만을 만끽하는 여행입니다.', 1200000.00, '2026-01-10', '2026-01-14', 6),
                                                                                                           (5, '런던 3박 4일 클래식 투어', '버킹엄 궁전, 타워 브리지, 대영 박물관 등 런던의 역사를 체험하는 클래식 투어입니다.', 1100000.00, '2026-02-05', '2026-02-08', 7),
                                                                                                           (6, '도쿄 3박 4일 미식 탐방', '츠키지 시장, 시부야, 신주쿠의 유명 맛집을 탐방하며 도쿄의 맛을 즐기는 여행입니다.', 950000.00, '2026-03-12', '2026-03-15', 8);

-- 패키지 이미지 데이터
INSERT INTO `package_image` (`id`, `image_url`, `package_id`) VALUES
                                                                  (1, '/images/package1/package1_1.png', 1), (2, '/images/package1/package1_2.png', 1),
                                                                  (3, '/images/package1/package1_3.png', 1), (4, '/images/package1/package1_4.png', 1),
                                                                  (5, '/images/package2/package2_1.png', 2), (6, '/images/package3/package3_1.png', 3),
                                                                  (7, '/images/package4/package4_1.png', 4), (8, '/images/package5/package5_1.png', 5),
                                                                  (9, '/images/package6/package6_1.png', 6);

-- 찜 목록 데이터
INSERT INTO `favorites` (`id`, `user_id`, `hotel_id`) VALUES
                                                          (1, 1, 2), (2, 1, 3), (3, 2, 1), (4, 3, 5), (5, 4, 8), (6, 5, 7);

-- 리뷰 데이터
INSERT INTO `review` (`id`, `user_id`, `hotel_id`, `content`, `user_rating_score`) VALUES
                                                                                       (1, 1, 1, '서비스가 매우 만족스러웠습니다. 직원들이 친절하고 시설도 깨끗합니다.', 4.5),
                                                                                       (2, 2, 2, '오션 뷰가 정말 멋졌어요. 다시 방문하고 싶습니다.', 5.0),
                                                                                       (3, 3, 3, '가족 여행에 최고였습니다. 아이들이 좋아했어요.', 4.0),
                                                                                       (4, 4, 4, '조용하고 편안한 분위기에서 잘 쉬었습니다.', 4.8),
                                                                                       (5, 5, 5, '가격 대비 만족도가 높았습니다. 추천합니다.', 4.2),
                                                                                       (6, 1, 6, '파리의 중심에서 럭셔리한 경험을 했습니다. 잊지 못할 거예요.', 5.0),
                                                                                       (7, 2, 7, '역사와 전통이 느껴지는 멋진 호텔이었습니다.', 4.7),
                                                                                       (8, 3, 8, '신주쿠의 야경이 한눈에 들어오는 전망이 최고였습니다.', 4.9);

-- 예약 데이터
INSERT INTO `reservation` (`id`, `user_id`, `room_id`, `check_in_date`, `check_out_date`, `discount`, `taxes`, `total_price`) VALUES
                                                                                                                                  (1, 1, 2, '2025-10-10', '2025-10-12', 50000.00, 30000.00, 680000.00),
                                                                                                                                  (2, 2, 3, '2025-11-15', '2025-11-18', 100000.00, 80000.00, 1180000.00),
                                                                                                                                  (3, 3, 5, '2025-12-20', '2025-12-22', 30000.00, 19000.00, 429000.00),
                                                                                                                                  (4, 4, 14, '2026-02-05', '2026-02-07', 70000.00, 83000.00, 1713000.00),
                                                                                                                                  (5, 5, 16, '2026-03-12', '2026-03-14', 20000.00, 168000.00, 1848000.00);

-- 결제 수단 데이터
INSERT INTO `payment` (`id`, `user_id`, `payment_name`, `payment_number`, `expiration_date`, `cvc`, `card_user`, `country`, `registration_date`) VALUES
                                                                                                                                                     (1, 1, '신한카드', '1234-5678-9012-3456', '2028-12-31', '123', '김민준', '대한민국', '2025-01-15 10:00:00'),
                                                                                                                                                     (2, 2, '국민카드', '2345-6789-0123-4567', '2027-11-30', '234', '이서연', '대한민국', '2025-02-20 11:30:00'),
                                                                                                                                                     (3, 3, '삼성카드', '3456-7890-1234-5678', '2029-10-31', '345', '박준호', '대한민국', '2025-03-25 14:00:00'),
                                                                                                                                                     (4, 4, '현대카드', '4567-8901-2345-6789', '2026-09-30', '456', '최지우', '대한민국', '2025-04-10 16:45:00'),
                                                                                                                                                     (5, 5, '우리카드', '5678-9012-3456-7890', '2028-08-31', '567', '정유진', '대한민국', '2025-05-12 18:20:00');

-- 결제 내역 데이터
INSERT INTO `pay` (`id`, `reservation_id`, `payment_id`, `user_id`, `payment_gateway`, `redate`, `price`) VALUES
                                                                                                              (1, 1, 1, 1, '카카오페이', '2025-10-01 14:00:00', 680000.00),
                                                                                                              (2, 2, 2, 2, '네이버페이', '2025-11-01 16:30:00', 1180000.00),
                                                                                                              (3, 3, 3, 3, '토스페이', '2025-12-01 18:00:00', 429000.00),
                                                                                                              (4, 4, 4, 4, '신용카드', '2026-01-20 20:15:00', 1713000.00),
                                                                                                              (5, 5, 5, 5, '계좌이체', '2026-02-20 22:00:00', 1848000.00);
