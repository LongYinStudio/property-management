-- =============================================
-- 住宅小区物业管理系统
-- =============================================

CREATE DATABASE IF NOT EXISTS property_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE property_management;

-- =============================================
-- 用户表
-- =============================================
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
    `role` TINYINT NOT NULL DEFAULT 3 COMMENT '角色：1-管理员 2-物业人员 3-业主',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `community_id` BIGINT DEFAULT NULL COMMENT '小区ID',
    `building_id` BIGINT DEFAULT NULL COMMENT '楼栋ID',
    `room_id` BIGINT DEFAULT NULL COMMENT '房间ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 初始化管理员账号（密码：admin）加密方式：Bcrypt
INSERT INTO `sys_user` (`username`, `password`, `real_name`, `phone`, `role`, `status`) VALUES
('admin', '$2b$10$C81z4XvP7pwVSSOGEPOmnugIhV9cVOmzl5aQrU2vOwSjUtr7rAlWe', '系统管理员', '13800138000', 1, 1);

-- =============================================
-- 小区表
-- =============================================
DROP TABLE IF EXISTS `community`;
CREATE TABLE `community` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '小区ID',
    `name` VARCHAR(100) NOT NULL COMMENT '小区名称',
    `address` VARCHAR(255) DEFAULT NULL COMMENT '小区地址',
    `area` DECIMAL(10,2) DEFAULT NULL COMMENT '占地面积',
    `build_year` INT DEFAULT NULL COMMENT '建成年份',
    `total_buildings` INT DEFAULT NULL COMMENT '楼栋总数',
    `total_rooms` INT DEFAULT NULL COMMENT '房间总数',
    `description` TEXT COMMENT '小区描述',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='小区表';

-- =============================================
-- 楼栋表
-- =============================================
DROP TABLE IF EXISTS `building`;
CREATE TABLE `building` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '楼栋ID',
    `community_id` BIGINT NOT NULL COMMENT '小区ID',
    `name` VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    `floors` INT DEFAULT NULL COMMENT '楼层数',
    `units` INT DEFAULT NULL COMMENT '单元数',
    `rooms_per_floor` INT DEFAULT NULL COMMENT '每层房间数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_community_id` (`community_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼栋表';

-- =============================================
-- 房间表
-- =============================================
DROP TABLE IF EXISTS `room`;
CREATE TABLE `room` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '房间ID',
    `building_id` BIGINT NOT NULL COMMENT '楼栋ID',
    `unit` VARCHAR(10) DEFAULT NULL COMMENT '单元',
    `floor` INT DEFAULT NULL COMMENT '楼层',
    `room_number` VARCHAR(20) NOT NULL COMMENT '房间号',
    `area` DECIMAL(10,2) DEFAULT NULL COMMENT '面积',
    `owner_id` BIGINT DEFAULT NULL COMMENT '业主ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-空置 1-已入住',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_building_id` (`building_id`),
    KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='房间表';

-- =============================================
-- 报修表
-- =============================================
DROP TABLE IF EXISTS `repair`;
CREATE TABLE `repair` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '报修ID',
    `user_id` BIGINT NOT NULL COMMENT '报修人ID',
    `title` VARCHAR(100) NOT NULL COMMENT '报修标题',
    `content` TEXT COMMENT '报修内容',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '图片地址（逗号分隔）',
    `type` TINYINT NOT NULL DEFAULT 4 COMMENT '类型：1-水管 2-电路 3-门窗 4-其他',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已完成 3-已关闭',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `handle_result` TEXT COMMENT '处理结果',
    `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='报修表';

-- =============================================
-- 清洁任务表
-- =============================================
DROP TABLE IF EXISTS `cleaning`;
CREATE TABLE `cleaning` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '清洁ID',
    `user_id` BIGINT NOT NULL COMMENT '提交人ID',
    `location` VARCHAR(100) NOT NULL COMMENT '清洁位置',
    `description` TEXT COMMENT '描述',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '图片地址',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理 1-处理中 2-已完成',
    `cleaner_id` BIGINT DEFAULT NULL COMMENT '清洁人员ID',
    `clean_result` TEXT COMMENT '清洁结果',
    `clean_time` DATETIME DEFAULT NULL COMMENT '清洁时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='清洁任务表';

-- =============================================
-- 物业费表
-- =============================================
DROP TABLE IF EXISTS `property_fee`;
CREATE TABLE `property_fee` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '费用ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `room_id` BIGINT DEFAULT NULL COMMENT '房间ID',
    `year` INT NOT NULL COMMENT '年份',
    `month` INT NOT NULL COMMENT '月份',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-物业费 2-停车费 3-水费 4-电费',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-未支付 1-已支付',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_room_id` (`room_id`),
    KEY `idx_status` (`status`),
    KEY `idx_year_month` (`year`, `month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='物业费表';

-- =============================================
-- 投诉建议表
-- =============================================
DROP TABLE IF EXISTS `complaint`;
CREATE TABLE `complaint` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '投诉ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `title` VARCHAR(100) NOT NULL COMMENT '标题',
    `content` TEXT COMMENT '内容',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-投诉 2-建议',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待处理 1-已回复 2-已关闭',
    `handler_id` BIGINT DEFAULT NULL COMMENT '处理人ID',
    `reply` TEXT COMMENT '回复内容',
    `reply_time` DATETIME DEFAULT NULL COMMENT '回复时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投诉建议表';

-- =============================================
-- 设备设施表
-- =============================================
DROP TABLE IF EXISTS `facility`;
CREATE TABLE `facility` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '设施ID',
    `name` VARCHAR(100) NOT NULL COMMENT '设施名称',
    `type` TINYINT NOT NULL COMMENT '类型：1-消防设施 2-电梯 3-监控 4-其他',
    `location` VARCHAR(255) DEFAULT NULL COMMENT '位置',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-故障 1-正常',
    `last_check_date` DATE DEFAULT NULL COMMENT '上次检查日期',
    `next_check_date` DATE DEFAULT NULL COMMENT '下次检查日期',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_type` (`type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备设施表';

-- =============================================
-- 设备巡检记录表
-- =============================================
DROP TABLE IF EXISTS `equipment_inspection`;
CREATE TABLE `equipment_inspection` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '巡检记录ID',
    `user_id` BIGINT NOT NULL COMMENT '巡检人ID',
    `equipment_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
    `equipment_type` TINYINT NOT NULL COMMENT '设备类型：1-消防设施 2-电梯 3-监控 4-给排水 5-配电 6-其他',
    `location` VARCHAR(255) NOT NULL COMMENT '设备位置',
    `inspection_date` DATE NOT NULL COMMENT '巡检日期',
    `next_inspection_date` DATE DEFAULT NULL COMMENT '下次巡检日期',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '巡检状态：0-正常 1-异常 2-已维修',
    `result` TEXT COMMENT '巡检结果',
    `issue_description` TEXT COMMENT '问题描述',
    `images` VARCHAR(1000) DEFAULT NULL COMMENT '图片地址',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_equipment_type` (`equipment_type`),
    KEY `idx_status` (`status`),
    KEY `idx_inspection_date` (`inspection_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备巡检记录表';

-- =============================================
-- 车位表
-- =============================================
DROP TABLE IF EXISTS `parking_space`;
CREATE TABLE `parking_space` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '车位ID',
    `space_number` VARCHAR(20) NOT NULL COMMENT '车位编号',
    `location` VARCHAR(100) DEFAULT NULL COMMENT '车位位置（如：地下停车场A区）',
    `type` TINYINT NOT NULL DEFAULT 1 COMMENT '类型：1-普通车位 2-VIP车位',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-空闲 1-已出租 2-已出售',
    `owner_id` BIGINT DEFAULT NULL COMMENT '业主ID（出售时关联）',
    `price` DECIMAL(10,2) DEFAULT NULL COMMENT '出售价格',
    `rent_price` DECIMAL(10,2) NOT NULL COMMENT '月租价格',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_space_number` (`space_number`),
    KEY `idx_status` (`status`),
    KEY `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车位表';

-- =============================================
-- 车位租用记录表
-- =============================================
DROP TABLE IF EXISTS `parking_rental`;
CREATE TABLE `parking_rental` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '租用记录ID',
    `space_id` BIGINT NOT NULL COMMENT '车位ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `start_date` DATE NOT NULL COMMENT '开始日期',
    `end_date` DATE NOT NULL COMMENT '结束日期',
    `amount` DECIMAL(10,2) NOT NULL COMMENT '金额',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待支付 1-已支付 2-已过期',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_space_id` (`space_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='车位租用记录表';
