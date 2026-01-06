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
           '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMp.H.NnKCU6',
           'System Administrator',
           'ADMIN',
           1
       );

-- Default Test User (Credentials: user01 / 123456)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES (
           'user01',
           '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMp.H.NnKCU6',
           'Standard Employee',
           'USER',
           1
       );

-- -----------------------------------------------------------------------------
-- Initialization Complete
-- -----------------------------------------------------------------------------