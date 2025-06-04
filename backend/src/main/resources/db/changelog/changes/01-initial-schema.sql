-- src/main/resources/db/changelog/changes/01-initial-schema.sql

-- liquibase formatted sql

-- changeset oyushik:1
-- comment: Create notifications table
CREATE TABLE IF NOT EXISTS `notifications` (
 `notification_id` BIGINT NOT NULL,
 `user_id` BIGINT NULL,
 `entity_id` BIGINT NULL,
 `entity_type` VARCHAR(20) NULL,
 `message` VARCHAR(255) NULL,
 `sent_at` TIMESTAMP NULL,
 `is_read` BOOLEAN NULL
);

-- changeset oyushik:2
-- comment: Add primary key to notifications table
ALTER TABLE `notifications` ADD CONSTRAINT `PK_NOTIFICATIONS` PRIMARY KEY (
 `notification_id`
);

-- changeset oyushik:3
-- comment: Create categories table
CREATE TABLE IF NOT EXISTS `categories` (
 `category_id` INT NOT NULL,
 `category_name` VARCHAR(20) NULL
);

-- changeset oyushik:4
-- comment: Add primary key to categories table
ALTER TABLE `categories` ADD CONSTRAINT `PK_CATEGORIES` PRIMARY KEY (
 `category_id`
);

-- changeset oyushik:5
-- comment: Create reports table
CREATE TABLE IF NOT EXISTS `reports` (
 `report_id` BIGINT NOT NULL,
 `report_reason_id` INT NULL,
 `user_id` BIGINT NULL,
 `target_id` BIGINT NULL,
 `target_type` VARCHAR(20) NULL,
 `report_detail` VARCHAR(255) NULL,
 `is_processed` BOOLEAN NULL
);

-- changeset oyushik:6
-- comment: Add primary key to reports table
ALTER TABLE `reports` ADD CONSTRAINT `PK_REPORTS` PRIMARY KEY (
 `report_id`
);

-- changeset oyushik:7
-- comment: Create bumps table
CREATE TABLE IF NOT EXISTS `bumps` (
 `bump_id` BIGINT NOT NULL,
 `product_id` BIGINT NULL,
 `bumped_at` TIMESTAMP NULL,
 `bump_count` INT NULL
);

-- changeset oyushik:8
-- comment: Add primary key to bumps table
ALTER TABLE `bumps` ADD CONSTRAINT `PK_BUMPS` PRIMARY KEY (
 `bump_id`
);

-- changeset oyushik:9
-- comment: Create dibs table
CREATE TABLE IF NOT EXISTS `dibs` (
 `dibs_id` BIGINT NOT NULL,
 `user_id` BIGINT NULL,
 `product_id` BIGINT NULL,
 `added_at` TIMESTAMP NULL
);

-- changeset oyushik:10
-- comment: Add primary key to dibs table
ALTER TABLE `dibs` ADD CONSTRAINT `PK_DIBS` PRIMARY KEY (
 `dibs_id`
);

-- changeset oyushik:11
-- comment: Create reservations table
CREATE TABLE IF NOT EXISTS `reservations` (
 `reservation_id` BIGINT NOT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `product_id` BIGINT NULL,
 `is_canceled` BOOLEAN NULL,
 `status` ENUM('REQUESTED', 'ACCEPTED', 'DECLINED', 'CANCELLED', 'COMPLETED') NULL
);

-- changeset oyushik:12
-- comment: Add primary key to reservations table
ALTER TABLE `reservations` ADD CONSTRAINT `PK_RESERVATIONS` PRIMARY KEY (
 `reservation_id`
);

-- changeset oyushik:13
-- comment: Create messages table
CREATE TABLE IF NOT EXISTS `messages` (
 `message_id` BIGINT NOT NULL,
 `chat_id` BIGINT NULL,
 `sender_id` BIGINT NULL,
 `content` VARCHAR(255) NOT NULL,
 `sent_at` TIMESTAMP NULL,
 `is_read` BOOLEAN NULL
);

-- changeset oyushik:14
-- comment: Add primary key to messages table
ALTER TABLE `messages` ADD CONSTRAINT `PK_MESSAGES` PRIMARY KEY (
 `message_id`
);

-- changeset oyushik:15
-- comment: Create images table
CREATE TABLE IF NOT EXISTS `images` (
 `image_id` BIGINT NOT NULL,
 `product_id` BIGINT NULL,
 `image_url` VARCHAR(255) NOT NULL
);

-- changeset oyushik:16
-- comment: Add primary key to images table
ALTER TABLE `images` ADD CONSTRAINT `PK_IMAGES` PRIMARY KEY (
 `image_id`
);

-- changeset oyushik:17
-- comment: Create users table
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

-- changeset oyushik:18
-- comment: Add primary key to users table
ALTER TABLE `users` ADD CONSTRAINT `PK_USERS` PRIMARY KEY (
 `user_id`
);

-- changeset oyushik:19
-- comment: Create bans table
CREATE TABLE IF NOT EXISTS `bans` (
 `ban_id` BIGINT NOT NULL,
 `user_id` BIGINT NULL,
 `report_id` BIGINT NULL,
 `ban_reason` VARCHAR(50) NULL
);

-- changeset oyushik:20
-- comment: Add primary key to bans table
ALTER TABLE `bans` ADD CONSTRAINT `PK_BANS` PRIMARY KEY (
 `ban_id`
);

-- changeset oyushik:21
-- comment: Create report_reasons table
CREATE TABLE IF NOT EXISTS `report_reasons` (
 `report_reason_id` INT NOT NULL,
 `report_reason_type` VARCHAR(50) NULL
);

-- changeset oyushik:22
-- comment: Add primary key to report_reasons table
ALTER TABLE `report_reasons` ADD CONSTRAINT `PK_REPORT_REASONS` PRIMARY KEY (
 `report_reason_id`
);

-- changeset oyushik:23
-- comment: Create areas table
CREATE TABLE IF NOT EXISTS `areas` (
 `area_id` INT NOT NULL,
 `area_name` VARCHAR(50) NULL
);

-- changeset oyushik:24
-- comment: Add primary key to areas table
ALTER TABLE `areas` ADD CONSTRAINT `PK_AREAS` PRIMARY KEY (
 `area_id`
);

-- changeset oyushik:25
-- comment: Create products table
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

-- changeset oyushik:26
-- comment: Add primary key to products table
ALTER TABLE `products` ADD CONSTRAINT `PK_PRODUCTS` PRIMARY KEY (
 `product_id`
);

-- changeset oyushik:27
-- comment: Create reviews table
CREATE TABLE IF NOT EXISTS `reviews` (
 `review_id` BIGINT NOT NULL,
 `complete_id` BIGINT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `rating` INT NOT NULL,
 `content` VARCHAR(255) NULL
);

-- changeset oyushik:28
-- comment: Add primary key to reviews table
ALTER TABLE `reviews` ADD CONSTRAINT `PK_REVIEWS` PRIMARY KEY (
 `review_id`
);

-- changeset oyushik:29
-- comment: Create completes table
CREATE TABLE IF NOT EXISTS `completes` (
 `complete_id` BIGINT NOT NULL,
 `product_id` BIGINT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `completed_at` TIMESTAMP NULL
);

-- changeset oyushik:30
-- comment: Add primary key to completes table
ALTER TABLE `completes` ADD CONSTRAINT `PK_COMPLETES` PRIMARY KEY (
 `complete_id`
);

-- changeset oyushik:31
-- comment: Create cancelations table
CREATE TABLE IF NOT EXISTS `cancelations` (
 `cancelation_id` BIGINT NOT NULL,
 `reservation_id` BIGINT NULL,
 `cancelation_reason_id` INT NULL,
 `canceler_id` BIGINT NULL,
 `cancelation_detail` VARCHAR(255) NULL,
 `canceled_at` TIMESTAMP NULL
);

-- changeset oyushik:32
-- comment: Add primary key to cancelations table
ALTER TABLE `cancelations` ADD CONSTRAINT `PK_CANCELATIONS` PRIMARY KEY (
 `cancelation_id`
);

-- changeset oyushik:33
-- comment: Create chats table
CREATE TABLE IF NOT EXISTS `chats` (
 `chat_id` BIGINT NOT NULL,
 `buyer_id` BIGINT NULL,
 `seller_id` BIGINT NULL,
 `product_id` BIGINT NULL
);

-- changeset oyushik:34
-- comment: Add primary key to chats table
ALTER TABLE `chats` ADD CONSTRAINT `PK_CHATS` PRIMARY KEY (
 `chat_id`
);

-- changeset oyushik:35
-- comment: Create cancelation_reasons table
CREATE TABLE IF NOT EXISTS `cancelation_reasons` (
 `cancelation_reason_id` INT NOT NULL,
 `cancelation_reason_type` VARCHAR(50) NULL
);

-- changeset oyushik:36
-- comment: Add primary key to cancelation_reasons table
ALTER TABLE `cancelation_reasons` ADD CONSTRAINT `PK_CANCELATION_REASONS` PRIMARY KEY (
 `cancelation_reason_id`
);
