-- 전화번호 인증 테이블
CREATE TABLE `verification` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `phone_number` varchar(13) COLLATE utf8mb4_unicode_ci NOT NULL unique,
                                `code` varchar(4) NOT NULL comment '전화번호 인증코드',
                                `send_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '전화번호 인증문자 보낸 시간',
                                `used` tinyint(1) DEFAULT '0' NOT NULL,
                                `fail_count` int DEFAULT '0' NOT NULL comment '전화번호 인증 실패 횟수 최대 3번 까지 가능함',
                                `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP comment '생성 시간',
                                `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP comment '수정 시간',
                                PRIMARY KEY (`id`)
);

-- 사용자 테이블
CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
                        `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
                        `birth` date NOT NULL,
                        `gender` ENUM('FEMAIL','MALE') NOT NULL,
                        `nickname` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE,
                        `phone_number` varchar(13) COLLATE utf8mb4_unicode_ci NOT NULL,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        PRIMARY KEY (`id`)
);

-- 이용약관 테이블
CREATE TABLE `term` (

                        `title` varchar(255) NOT NULL COLLATE utf8mb4_unicode_ci ,
                        `version` varchar(4) NOT NULL COLLATE utf8mb4_unicode_ci,
                        `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `updated_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `required` tinyint(1) NOT NULL DEFAULT '1' COMMENT '약관 필수 여부 (1= 필수, 0=선택)',
                        `active` tinyint(1) NOT NULL DEFAULT '1' COMMENT '현재 사용 중 여부 (1= 사용중, 0=이전 버전)' ,
                        `created_by` varchar(5) NOT NULL,
                        `updated_by` varchar(5) NOT NULL,
                        `lockVersion` int,
                        PRIMARY KEY (`title`,`version`)
);

-- 사용자 이용약관 동의 내역 table
CREATE TABLE `agreement` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `user_id` BIGINT NOT NULL,
                             `title` VARCHAR(255) NOT NULL COLLATE utf8mb4_unicode_ci,
                             `version` VARCHAR(4) NOT NULL COLLATE utf8mb4_unicode_ci,
                             `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             `status` ENUM('AGREED', 'WITHDRAWN') NOT NULL COMMENT '약관 동의/철회',

                             PRIMARY KEY (`id`),
                             UNIQUE KEY `uniq_user_term` (`user_id`, `title`, `version`),

                             KEY `idx_term` (`title`, `version`),

                             CONSTRAINT `fk_agreement_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
                             CONSTRAINT `fk_agreement_term` FOREIGN KEY (`title`, `version`) REFERENCES `term` (`title`, `version`) ON DELETE CASCADE
);