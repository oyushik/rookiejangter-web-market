-- src/main/resources/db/migration/V1__initial_schema.sql

-- notifications table
CREATE TABLE IF NOT EXISTS `notifications` (
  `notification_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NULL,
  `entity_id` BIGINT NULL,
  `entity_type` VARCHAR(20) NULL,
  `message` VARCHAR(255) NULL,
  `is_read` BOOLEAN NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- categories table
CREATE TABLE IF NOT EXISTS `categories` (
  `category_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `category_name` VARCHAR(20) NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- reports table
CREATE TABLE IF NOT EXISTS `reports` (
  `report_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `report_reason_id` INT NULL,
  `user_id` BIGINT NULL,
  `target_id` BIGINT NULL,
  `target_type` VARCHAR(20) NULL,
  `report_detail` VARCHAR(255) NULL,
  `is_processed` BOOLEAN NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- dibs table
CREATE TABLE IF NOT EXISTS `dibs` (
  `dibs_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NULL,
  `product_id` BIGINT NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- reservations table
CREATE TABLE IF NOT EXISTS `reservations` (
  `reservation_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `buyer_id` BIGINT NULL,
  `seller_id` BIGINT NULL,
  `product_id` BIGINT NULL,
  `chat_id` BIGINT NULL,
  `is_canceled` BOOLEAN NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- messages table
CREATE TABLE IF NOT EXISTS `messages` (
  `message_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `chat_id` BIGINT NULL,
  `sender_id` BIGINT NULL,
  `content` VARCHAR(255) NOT NULL,
  `is_read` BOOLEAN NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- images table
CREATE TABLE IF NOT EXISTS `images` (
  `image_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- users table
CREATE TABLE IF NOT EXISTS `users` (
  `user_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `area_id` INT NULL,
  `login_id` VARCHAR(20) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `user_name` VARCHAR(12) NOT NULL,
  `phone` VARCHAR(12) NOT NULL,
  `is_banned` BOOLEAN NULL,
  `is_admin` BOOLEAN NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- bans table
CREATE TABLE IF NOT EXISTS `bans` (
  `ban_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NULL,
  `report_id` BIGINT NULL,
  `ban_reason` VARCHAR(50) NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- report reasons table
CREATE TABLE IF NOT EXISTS `report_reasons` (
  `report_reason_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `report_reason_type` VARCHAR(50) NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- areas table
CREATE TABLE IF NOT EXISTS `areas` (
  `area_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `area_name` VARCHAR(50) NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- products table
CREATE TABLE IF NOT EXISTS `products` (
  `product_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `category_id` INT NULL,
  `user_id` BIGINT NULL,
  `title` VARCHAR(50) NOT NULL,
  `content` VARCHAR(255) NOT NULL,
  `price` INT NULL,
  `view_count` INT NULL,
  `is_bumped` BOOLEAN NULL,
  `is_reserved` BOOLEAN NULL,
  `is_completed` BOOLEAN NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- reviews table
CREATE TABLE IF NOT EXISTS `reviews` (
  `review_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `complete_id` BIGINT NULL,
  `buyer_id` BIGINT NULL,
  `seller_id` BIGINT NULL,
  `rating` INT NOT NULL,
  `content` VARCHAR(255) NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- completes table
CREATE TABLE IF NOT EXISTS `completes` (
  `complete_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `product_id` BIGINT NULL,
  `buyer_id` BIGINT NULL,
  `seller_id` BIGINT NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- cancelations table
CREATE TABLE IF NOT EXISTS `cancelations` (
  `cancelation_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `cancelation_reason_id` INT NULL,
  `product_id` BIGINT NULL,
  `buyer_id` BIGINT NULL,
  `seller_id` BIGINT NULL,
  `is_canceled_by_buyer` BOOLEAN NULL,
  `cancelation_detail` VARCHAR(255) NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- chats table
CREATE TABLE IF NOT EXISTS `chats` (
  `chat_id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `buyer_id` BIGINT NULL,
  `seller_id` BIGINT NULL,
  `product_id` BIGINT NULL,
  `reservation_id` BIGINT NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);

-- cancelation reasons table
CREATE TABLE IF NOT EXISTS `cancelation_reasons` (
  `cancelation_reason_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `cancelation_reason_type` VARCHAR(50) NULL,
  `is_cancelation_reason_of_buyer` BOOLEAN NULL,
  `created_at` TIMESTAMP NULL,
  `updated_at` TIMESTAMP NULL
);
