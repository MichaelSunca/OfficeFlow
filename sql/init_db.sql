-- =============================================================================
-- OfficeFlow Database Initialization Script
-- Target: MySQL 8.0+
-- Encoding: UTF-8 MB4 (Supports Japanese characters & Emojis)
-- =============================================================================

-- 1. Create Database
-- -----------------------------------------------------------------------------
CREATE DATABASE IF NOT EXISTS `office_flow`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE `office_flow`;

-- 2. Drop existing tables to ensure a clean setup
-- -----------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `sys_user`;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. Create System User Table
-- -----------------------------------------------------------------------------
CREATE TABLE `sys_user` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
                            `username` VARCHAR(50) NOT NULL UNIQUE COMMENT 'Username or Employee ID used for login',
                            `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt encoded password',
                            `nickname` VARCHAR(50) DEFAULT NULL COMMENT 'Full name of the employee',
                            `role` VARCHAR(20) DEFAULT 'USER' COMMENT 'Role-Based Access: ADMIN, USER',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Record creation timestamp',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
                            `status` TINYINT DEFAULT 1 COMMENT 'Account status: 1=Active, 0=Disabled',
                            PRIMARY KEY (`id`),
                            INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System User Management';

-- 4. Seed Initial Data
-- -----------------------------------------------------------------------------
-- Default Admin User (Credentials: admin / 123456)
-- Password hashed using BCrypt
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES (
           'admin',
           '$2a$10$hG8cZC2n8YgH8w6bGq7w0uW3rXb8f0p0qzG8p6F6ZqkQ8sJ0yKQm2',
           'System Administrator',
           'ADMIN',
           1
       );

-- Default Test User (Credentials: user01 / 123456)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES (
           'user01',
           '$2a$10$hG8cZC2n8YgH8w6bGq7w0uW3rXb8f0p0qzG8p6F6ZqkQ8sJ0yKQm2',
           'Standard Employee',
           'USER',
           1
       );

-- 5. Create Asset Table (资产信息表)
-- -----------------------------------------------------------------------------
CREATE TABLE `bus_asset` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
                             `asset_name` VARCHAR(100) NOT NULL COMMENT 'Asset Name (e.g., Laptop, Chair)',
                             `asset_sn` VARCHAR(50) DEFAULT NULL UNIQUE COMMENT 'Unique Serial Number',
                             `category` VARCHAR(30) DEFAULT 'General' COMMENT 'Category: Electronics, Furniture, etc.',
                             `price` DECIMAL(10, 2) DEFAULT 0.00 COMMENT 'Purchase Price',
                             `status` TINYINT DEFAULT 0 COMMENT 'Status: 0=Idle, 1=In Use, 2=Under Repair, 3=Scrapped',
                             `user_id` BIGINT DEFAULT NULL COMMENT 'Current holder (Link to sys_user.id)',
                             `location` VARCHAR(100) DEFAULT NULL COMMENT 'Physical Location',
                             `purchase_date` DATE DEFAULT NULL COMMENT 'Date of purchase',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation time',
                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
                             PRIMARY KEY (`id`),
                             INDEX `idx_asset_sn` (`asset_sn`),
                             INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Asset Information';

-- 6. Create Asset Record Table (资产流转/审批记录表)
-- -----------------------------------------------------------------------------
CREATE TABLE `bus_record` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'Primary Key ID',
                              `asset_id` BIGINT NOT NULL COMMENT 'Target Asset ID',
                              `user_id` BIGINT NOT NULL COMMENT 'Applicant/Operator ID',
                              `action_type` VARCHAR(20) NOT NULL COMMENT 'Action: APPLY, RETURN, REPAIR',
                              `remark` TEXT DEFAULT NULL COMMENT 'Reason or extra notes',
                              `audit_status` TINYINT DEFAULT 0 COMMENT '0=Pending, 1=Approved, 2=Rejected',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Log creation time',
                              PRIMARY KEY (`id`),
                              INDEX `idx_asset_id` (`asset_id`),
                              INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Asset Flow & Audit Records';

-- 7. Seed Sample Assets
-- -----------------------------------------------------------------------------
INSERT INTO `bus_asset` (`asset_name`, `asset_sn`, `category`, `price`, `status`, `location`)
VALUES
    ('MacBook Pro 14"', 'SN2026001', 'Electronics', 250000.00, 0, 'Storage Room A'),
    ('Herman Miller Aeron', 'SN2026002', 'Furniture', 180000.00, 1, 'Office Area B');

-- -----------------------------------------------------------------------------
-- Initialization Complete
-- -----------------------------------------------------------------------------