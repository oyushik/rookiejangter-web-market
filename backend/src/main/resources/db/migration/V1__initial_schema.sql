-- src/main/resources/db/migration/V1__initial_schema.sql

CREATE TABLE IF NOT EXISTS `notifications` (
 `notification_id` BIGINT NOT NULL,
 `user_id` BIGINT NULL,
 `entity_id` BIGINT NULL,
 `entity_type` VARCHAR(20) NULL,
 `message` VARCHAR(255) NULL,
 `sent_at` TIMESTAMP NULL,
 `is_read` BOOLEAN NULL
);

ALTER TABLE `notifications` ADD CONSTRAINT `PK_NOTIFICATIONS` PRIMARY KEY (
 `notification_id`
);

CREATE TABLE IF NOT EXISTS `categories` (
 `category_id` INT NOT NULL,
 `category_name` VARCHAR(20) NULL
);

ALTER TABLE `categories` ADD CONSTRAINT `PK_CATEGORIES` PRIMARY KEY (
 `category_id`
);

CREATE TABLE IF NOT EXISTS `reports` (
 `report_id` BIGINT NOT NULL,
 `report_reason_id` INT NULL,
 `user_id` BIGINT NULL,
 `target_id` BIGINT NULL,
 `target_type` VARCHAR(20) NULL,
 `report_detail` VARCHAR(255) NULL,
 `is_processed` BOOLEAN NULL
);

ALTER TABLE `reports` ADD CONSTRAINT `PK_REPORTS` PRIMARY KEY (
 `report_id`
);

CREATE TABLE IF NOT EXISTS `bumps` (
 `bump_id` BIGINT NOT NULL,
 `product_id` BIGINT NULL,
 `bumped_at` TIMESTAMP NULL,
 `bump_count` INT NULL
);

ALTER TABLE `bumps` ADD CONSTRAINT `PK_BUMPS` PRIMARY KEY (
 `bump_id`
);

CREATE TABLE IF NOT EXISTS `dibs` (
 `dibs_id` BIGINT NOT NULL,
 `user_id` BIGINT NULL,
 `product_id` BIGINT NULL,
 `added_at` TIMESTAMP NULL
);

ALTER TABLE `dibs` ADD CONSTRAINT `PK_DIBS` PRIMARY KEY (
 `dibs_id`
);

CREATE TABLE IF NOT EXISTS `reservations` (
 `reservation_id` BIGINT NOT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `product_id` BIGINT NULL,
 `is_canceled` BOOLEAN NULL,
 `status` ENUM('REQUESTED', 'ACCEPTED', 'DECLINED', 'CANCELLED', 'COMPLETED') NULL
);

ALTER TABLE `reservations` ADD CONSTRAINT `PK_RESERVATIONS` PRIMARY KEY (
 `reservation_id`
);

CREATE TABLE IF NOT EXISTS `messages` (
 `message_id` BIGINT NOT NULL,
 `chat_id` BIGINT NULL,
 `sender_id` BIGINT NULL,
 `content` VARCHAR(255) NOT NULL,
 `sent_at` TIMESTAMP NULL,
 `is_read` BOOLEAN NULL
);

ALTER TABLE `messages` ADD CONSTRAINT `PK_MESSAGES` PRIMARY KEY (
 `message_id`
);

CREATE TABLE IF NOT EXISTS `images` (
 `image_id` BIGINT NOT NULL,
 `product_id` BIGINT NULL,
 `image_url` VARCHAR(255) NOT NULL
);

ALTER TABLE `images` ADD CONSTRAINT `PK_IMAGES` PRIMARY KEY (
 `image_id`
);

CREATE TABLE IF NOT EXISTS `users` (
 `user_id` BIGINT NOT NULL,
 `area_id` INT NULL,
 `login_id` VARCHAR(20) NOT NULL,
 `password` VARCHAR(20) NOT NULL,
 `user_name` VARCHAR(12) NOT NULL,
 `phone` VARCHAR(20) NOT NULL,
 `is_banned` BOOLEAN NULL,
 `is_admin` BOOLEAN NULL
);

ALTER TABLE `users` ADD CONSTRAINT `PK_USERS` PRIMARY KEY (
 `user_id`
);

CREATE TABLE IF NOT EXISTS `bans` (
 `ban_id` BIGINT NOT NULL,
 `user_id` BIGINT NULL,
 `report_id` BIGINT NULL,
 `ban_reason` VARCHAR(50) NULL
);

ALTER TABLE `bans` ADD CONSTRAINT `PK_BANS` PRIMARY KEY (
 `ban_id`
);

CREATE TABLE IF NOT EXISTS `report_reasons` (
 `report_reason_id` INT NOT NULL,
 `report_reason_type` VARCHAR(50) NULL
);

ALTER TABLE `report_reasons` ADD CONSTRAINT `PK_REPORT_REASONS` PRIMARY KEY (
 `report_reason_id`
);

CREATE TABLE IF NOT EXISTS `areas` (
 `area_id` INT NOT NULL,
 `area_name` VARCHAR(50) NULL
);

ALTER TABLE `areas` ADD CONSTRAINT `PK_AREAS` PRIMARY KEY (
 `area_id`
);

CREATE TABLE IF NOT EXISTS `products` (
 `product_id` BIGINT NOT NULL,
 `category_id` INT NULL,
 `user_id` BIGINT NULL,
 `title` VARCHAR(50) NOT NULL,
 `content` VARCHAR(255) NOT NULL,
 `price` INT NULL,
 `view_count` INT NULL,
 `is_bumped` BOOLEAN NULL,
 `is_reserved` BOOLEAN NULL,
 `is_completed` BOOLEAN NULL
);

ALTER TABLE `products` ADD CONSTRAINT `PK_PRODUCTS` PRIMARY KEY (
 `product_id`
);

CREATE TABLE IF NOT EXISTS `reviews` (
 `review_id` BIGINT NOT NULL,
 `complete_id` BIGINT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `rating` INT NOT NULL,
 `content` VARCHAR(255) NULL
);

ALTER TABLE `reviews` ADD CONSTRAINT `PK_REVIEWS` PRIMARY KEY (
 `review_id`
);

CREATE TABLE IF NOT EXISTS `completes` (
 `complete_id` BIGINT NOT NULL,
 `product_id` BIGINT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `completed_at` TIMESTAMP NULL
);

ALTER TABLE `completes` ADD CONSTRAINT `PK_COMPLETES` PRIMARY KEY (
 `complete_id`
);

CREATE TABLE IF NOT EXISTS `cancelations` (
 `cancelation_id` BIGINT NOT NULL,
 `reservation_id` BIGINT NULL,
 `cancelation_reason_id` INT NULL,
 `canceler_id` BIGINT NULL,
 `cancelation_detail` VARCHAR(255) NULL,
 `canceled_at` TIMESTAMP NULL
);

ALTER TABLE `cancelations` ADD CONSTRAINT `PK_CANCELATIONS` PRIMARY KEY (
 `cancelation_id`
);

CREATE TABLE IF NOT EXISTS `chats` (
 `chat_id` BIGINT NOT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `product_id` BIGINT NULL
);

ALTER TABLE `chats` ADD CONSTRAINT `PK_CHATS` PRIMARY KEY (
 `chat_id`
);

CREATE TABLE IF NOT EXISTS `cancelation_reasons` (
 `cancelation_reason_id` INT NOT NULL,
 `cancelation_reason_type` VARCHAR(50) NULL
);

ALTER TABLE `cancelation_reasons` ADD CONSTRAINT `PK_CANCELATION_REASONS` PRIMARY KEY (
 `cancelation_reason_id`
);
