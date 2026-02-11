-- 전화번호 인증 테이블
CREATE TABLE `verification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `phone_number` VARCHAR(13) NOT NULL UNIQUE,
    `code` VARCHAR(6) NOT NULL COMMENT '전화번호 인증코드',
    `send_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '전화번호 인증문자 보낸 시간',
    `used` TINYINT(1) NOT NULL DEFAULT 0,
    `fail_count` INT NOT NULL DEFAULT 0 COMMENT '전화번호 인증 실패 횟수 최대 3번 까지 가능함',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성 시간',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정 시간',
    PRIMARY KEY (`id`)
);

-- 사용자 테이블
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL UNIQUE,
    `password` VARCHAR(255),
    `birth` DATE,
    `gender` ENUM('MALE', 'FEMALE'),
    `nickname` VARCHAR(255) NOT NULL UNIQUE,
    `phone_number` VARCHAR(255),
    `role` ENUM('GUEST', 'USER', 'ADMIN') NOT NULL,
    `provider` VARCHAR(255) COMMENT 'google, github 등',
    `provider_id` VARCHAR(255) COMMENT '소셜 로그인의 sub 등 고유 식별자',
    `bio` TEXT COMMENT '자기소개',
    `lifestyle` TEXT COMMENT '라이프스타일/성향',
    `profile_image` VARCHAR(255) COMMENT '프로필 이미지 URL',
    `trust_score` DOUBLE NOT NULL DEFAULT 50.0 COMMENT '신뢰 점수',
    `host_rating` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '호스트 평점',
    `host_review_count` INT NOT NULL DEFAULT 0 COMMENT '호스트 리뷰 개수',
    `guest_rating` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '게스트 평점',
    `guest_review_count` INT NOT NULL DEFAULT 0 COMMENT '게스트 리뷰 개수',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);

-- 사용자 사용 언어 테이블
CREATE TABLE `user_languages` (
    `user_id` BIGINT NOT NULL,
    `language` ENUM('KOREAN', 'ENGLISH', 'JAPANESE', 'CHINESE', 'FRENCH', 'GERMAN', 'SPANISH') NOT NULL,
    PRIMARY KEY (`user_id`, `language`),
    CONSTRAINT `fk_user_languages_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);

-- 사용자 관심사 테이블
CREATE TABLE `user_interests` (
    `user_id` BIGINT NOT NULL,
    `interest` ENUM('BOARD_GAME', 'READING', 'HIKING', 'COOKING', 'TRAVELING', 'PHOTOGRAPHY', 'MUSIC', 'MOVIE', 'SPORTS') NOT NULL,
    PRIMARY KEY (`user_id`, `interest`),
    CONSTRAINT `fk_user_interests_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
);

-- 이용약관 테이블
CREATE TABLE `term` (
    `title` VARCHAR(255) NOT NULL,
    `version` VARCHAR(4) NOT NULL,
    `required` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '약관 필수 여부 (1= 필수, 0=선택)',
    `active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '현재 사용 중 여부 (1= 사용중, 0=이전 버전)',
    `created_by` VARCHAR(5) NOT NULL,
    `updated_by` VARCHAR(5) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`title`, `version`)
);

-- 사용자 이용약관 동의 내역 테이블
CREATE TABLE `agreement` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `title` VARCHAR(255) NOT NULL,
    `version` VARCHAR(4) NOT NULL,
    `status` ENUM('AGREED', 'WITHDRAWN') NOT NULL COMMENT '약관 동의/철회',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_user_term` (`user_id`, `title`, `version`),
    CONSTRAINT `fk_agreement_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_agreement_term` FOREIGN KEY (`title`, `version`) REFERENCES `term` (`title`, `version`) ON DELETE CASCADE
);

-- 숙소 테이블
CREATE TABLE `room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `host_id` BIGINT NOT NULL COMMENT '호스트 유저 ID',
    `title` VARCHAR(255) NOT NULL COMMENT '숙소 제목',
    `description` TEXT COMMENT '숙소 상세 설명',
    `image_url` VARCHAR(255) COMMENT '숙소 대표 사진 경로',
    `location` VARCHAR(255) NOT NULL COMMENT '대략적인 위치',
    `price` INT NOT NULL COMMENT '1박당 가격',
    `capacity` INT NOT NULL COMMENT '최대 수용 인원',
    `room_type` ENUM('COUCH', 'PRIVATE_ROOM', 'SHARED_ROOM') NOT NULL,
    `preferred_gender` ENUM('ANY', 'MALE', 'FEMALE') NOT NULL,
    `initial_question` TEXT COMMENT '호스트가 게스트에게 묻는 첫 질문',
    `is_active` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '숙소 활성화 상태',
    `is_deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '삭제 여부 (Soft Delete)',
    `host_rating` DOUBLE NOT NULL DEFAULT 0.0 COMMENT '호스트의 최신 신뢰 평점 (캐싱용)',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_room_host` (`host_id`)
);

-- 숙소 이미지 테이블
CREATE TABLE `room_image` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `room_id` BIGINT NOT NULL,
    `image_url` VARCHAR(255) NOT NULL,
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '이미지 출력 순서',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_room_image_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE
);

-- 숙소 편의시설 테이블
CREATE TABLE `room_amenity` (
    `room_id` BIGINT NOT NULL,
    `amenity_type` ENUM('WIFI', 'KITCHEN', 'WASHING_MACHINE', 'AIR_CONDITIONING', 'HEATING', 'TOWEL', 'SHAMPOO', 'HAIR_DRYER', 'PARKING', 'PETS_ALLOWED', 'SMOKING_ALLOWED') NOT NULL,
    PRIMARY KEY (`room_id`, `amenity_type`),
    CONSTRAINT `fk_room_amenity_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE
);

-- 예약 테이블
CREATE TABLE `reservation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL COMMENT '예약자 유저 ID',
    `room_id` BIGINT NOT NULL COMMENT '예약한 숙소 ID',
    `check_in_date` DATE NOT NULL,
    `check_out_date` DATE NOT NULL,
    `total_price` INT NOT NULL,
    `status` ENUM('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED') NOT NULL COMMENT '예약 상태',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_reservation_user` (`user_id`),
    KEY `idx_reservation_room` (`room_id`)
);