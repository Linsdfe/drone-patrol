-- 无人机巡防管控系统数据库初始化脚本
-- 数据库名：drone_patrol
-- 创建时间：2026-04-23

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS drone_patrol DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE drone_patrol;

-- 用户表
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '账号',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN-管理员，USER-普通用户',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统用户表';

-- 操作日志表
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '操作人ID',
  `username` varchar(50) NOT NULL COMMENT '操作人账号',
  `operation` varchar(100) NOT NULL COMMENT '操作内容',
  `ip` varchar(50) DEFAULT NULL COMMENT '操作IP',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志表';

-- 无人机设备表
DROP TABLE IF EXISTS `drone_device`;
CREATE TABLE `drone_device` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `device_code` varchar(50) NOT NULL COMMENT '设备编号（唯一）',
  `device_name` varchar(100) NOT NULL COMMENT '设备名称',
  `device_model` varchar(100) DEFAULT NULL COMMENT '设备型号',
  `battery_life` int DEFAULT NULL COMMENT '续航时长（分钟）',
  `camera_param` varchar(200) DEFAULT NULL COMMENT '摄像头参数',
  `owner` varchar(50) DEFAULT NULL COMMENT '归属人',
  `status` varchar(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态：NORMAL-正常，MAINTENANCE-维修中，SCRAPPED-已报废，IN_TASK-任务中',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_device_code` (`device_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='无人机设备表';

-- 巡防航线表
DROP TABLE IF EXISTS `patrol_route`;
CREATE TABLE `patrol_route` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_name` varchar(100) NOT NULL COMMENT '航线名称',
  `points` text NOT NULL COMMENT '航线点位JSON数组',
  `expected_duration` int DEFAULT NULL COMMENT '预计巡防时长（分钟）',
  `flight_height` int DEFAULT NULL COMMENT '飞行高度（米）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡防航线表';

-- 巡防任务表
DROP TABLE IF EXISTS `patrol_task`;
CREATE TABLE `patrol_task` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `device_id` bigint NOT NULL COMMENT '关联设备ID',
  `route_id` bigint NOT NULL COMMENT '关联航线ID',
  `executor_id` bigint NOT NULL COMMENT '执行人ID',
  `executor_name` varchar(50) NOT NULL COMMENT '执行人姓名',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING-待执行，EXECUTING-执行中，COMPLETED-已完成，CANCELLED-已取消',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `progress` int DEFAULT 0 COMMENT '执行进度（0-100）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_route_id` (`route_id`),
  KEY `idx_executor_id` (`executor_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡防任务表';

-- 巡防结果表
DROP TABLE IF EXISTS `patrol_result`;
CREATE TABLE `patrol_result` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `result_code` varchar(50) NOT NULL COMMENT '结果编号（唯一）',
  `task_id` bigint NOT NULL COMMENT '关联任务ID',
  `device_id` bigint NOT NULL COMMENT '关联设备ID',
  `route_id` bigint NOT NULL COMMENT '关联航线ID',
  `executor_id` bigint NOT NULL COMMENT '执行人ID',
  `executor_name` varchar(50) NOT NULL COMMENT '执行人姓名',
  `complete_time` datetime NOT NULL COMMENT '完成时间',
  `summary` varchar(1000) DEFAULT NULL COMMENT '巡防概述',
  `discovery` text DEFAULT NULL COMMENT '发现情况',
  `handling` text DEFAULT NULL COMMENT '处理情况',
  `ai_result` text DEFAULT NULL COMMENT 'AI识别结果（预留）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_result_code` (`result_code`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_device_id` (`device_id`),
  KEY `idx_route_id` (`route_id`),
  KEY `idx_executor_id` (`executor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='巡防结果表';

-- 注意：默认账号将在应用启动时自动创建
-- 管理员账号：admin / admin123
-- 普通用户账号：user / admin123

SET FOREIGN_KEY_CHECKS = 1;
