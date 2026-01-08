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

-- 2. Clean Up: Drop existing tables in reverse order of dependency
-- -----------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `bus_record`;
DROP TABLE IF EXISTS `bus_asset`;
DROP TABLE IF EXISTS `sys_user`;
SET FOREIGN_KEY_CHECKS = 1;

-- 3. Create System User Table
-- -----------------------------------------------------------------------------
CREATE TABLE `sys_user` (
                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
                            `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号或工号',
                            `password` VARCHAR(100) NOT NULL COMMENT 'BCrypt 加密密码',
                            `nickname` VARCHAR(50) DEFAULT NULL COMMENT '员工姓名',
                            `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色: ADMIN, USER',
                            `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                            `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            `status` TINYINT DEFAULT 1 COMMENT '账号状态: 1=正常, 0=禁用',
                            PRIMARY KEY (`id`),
                            INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户信息表';

-- -----------------------------------------------------------------------------
-- Table: bus_asset (资产台账表)
-- Description: 存储公司所有固定资产的核心信息
-- -----------------------------------------------------------------------------
CREATE TABLE `bus_asset` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
                             `asset_name` VARCHAR(100) NOT NULL COMMENT '资产名称',
                             `asset_sn` VARCHAR(50) NOT NULL COMMENT '资产序列号/机身码 (唯一标识)',
                             `category` VARCHAR(30) DEFAULT 'General' COMMENT '分类: 如 IT设备, 办公家具, 行政用品',
                            -- 价格处理：日元习惯或兼容多币种，使用 12 位，保留 2 位小数
                             `price` DECIMAL(12, 2) DEFAULT 0.00 COMMENT '采购价格',
                            -- 状态控制：0=闲置, 1=领用中, 2=维修, 3=报废
                             `status` TINYINT DEFAULT 0 COMMENT '资产状态',
                            -- 关联信息
                             `user_id` BIGINT DEFAULT NULL COMMENT '当前持有者/领用人 ID (关联 sys_user.id)',
                             `location` VARCHAR(100) DEFAULT NULL COMMENT '存放地点',
                             `purchase_date` DATE DEFAULT NULL COMMENT '采购日期',
                            -- 审计字段 (简历亮点：展示对系统可追溯性的理解)
                             `create_by` VARCHAR(50) DEFAULT NULL COMMENT '创建者账号',
                             `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             `update_by` VARCHAR(50) DEFAULT NULL COMMENT '更新者账号',
                             `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                            -- 逻辑删除 (简历亮点：企业级数据保护，防止误删)
                             `del_flag` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0=正常, 1=已删除',
                             PRIMARY KEY (`id`),
                             UNIQUE INDEX `uk_asset_sn` (`asset_sn`), -- 唯一索引防止重复录入
                             INDEX `idx_user_id` (`user_id`),
                             INDEX `idx_status` (`status`),
                             INDEX `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资产台账表';

-- 5. Create Asset Record Table (资产流转记录表)
-- -----------------------------------------------------------------------------
CREATE TABLE `bus_record` (
                              `id`          BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                              `asset_id`    BIGINT NOT NULL COMMENT '关联资产ID',
                              `user_id`     BIGINT NOT NULL COMMENT '操作人ID（发起人）',
                              `action_type` VARCHAR(20) NOT NULL COMMENT '动作类型: CLAIM(领用), RETURN(退库), REPAIR(报修), ADD(新增), DELETE(删除)',
                              `old_status`  TINYINT DEFAULT NULL COMMENT '变更前状态: 0-闲置, 1-领用, 2-维修',
                              `new_status`  TINYINT DEFAULT NULL COMMENT '变更后状态: 同上',
                              `audit_status` TINYINT DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-已通过, 2-已驳回',
                              `remark`      TEXT DEFAULT NULL COMMENT '操作备注',
                              `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
                              INDEX `idx_asset_id` (`asset_id`),
                              INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产流转历史记录表';

-- 6. Seed Initial Data
-- -----------------------------------------------------------------------------

-- 系统用户 (密码均为 123456)
INSERT INTO `sys_user` (`username`, `password`, `nickname`, `role`, `status`)
VALUES
    ('admin', '$2a$10$ZwsWSvK14ulHCw.sxFB/zetSxoknalsSFoEU9PKLBmnuiShN6TRCW', 'System Admin', 'ADMIN', 1),
    ('user01', '$2a$10$ZwsWSvK14ulHCw.sxFB/zetSxoknalsSFoEU9PKLBmnuiShN6TRCW', 'Standard Employee', 'USER', 1);


-- -----------------------------------------------------------------------------
-- Initialization Complete
-- -----------------------------------------------------------------------------