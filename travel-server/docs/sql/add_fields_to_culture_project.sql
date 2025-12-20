-- 为culture_project表添加扩展字段
-- 执行时间：2024-01-01

-- 添加邮箱字段
ALTER TABLE `culture_project` 
ADD COLUMN `email` VARCHAR(100) NULL COMMENT '联系邮箱' AFTER `contact_phone`;

-- 添加开始时间字段（存储年月格式：YYYY-MM）
ALTER TABLE `culture_project` 
ADD COLUMN `start_date` VARCHAR(7) NULL COMMENT '项目开始时间（年月，格式：YYYY-MM）' AFTER `create_time`;

-- 添加惠及农户数字段
ALTER TABLE `culture_project` 
ADD COLUMN `beneficiaries` INT DEFAULT 0 COMMENT '惠及农户数（户）' AFTER `price`;

-- 添加项目标签字段（JSON字符串）
ALTER TABLE `culture_project` 
ADD COLUMN `tags` TEXT NULL COMMENT '项目标签（JSON数组，如：["乡村振兴","产业融合"]）' AFTER `description`;

-- 添加项目目标字段（JSON字符串）
ALTER TABLE `culture_project` 
ADD COLUMN `goals` TEXT NULL COMMENT '项目目标（JSON数组，如：[{"title":"产业发展","description":"..."}]）' AFTER `tags`;

-- 添加合作方式字段（JSON字符串）
ALTER TABLE `culture_project` 
ADD COLUMN `cooperation` TEXT NULL COMMENT '合作方式（JSON数组，如：["政府提供政策支持","企业负责运营"]）' AFTER `goals`;

-- 添加政策支持字段（JSON字符串）
ALTER TABLE `culture_project` 
ADD COLUMN `policies` TEXT NULL COMMENT '政策支持（JSON数组，如：[{"title":"资金支持","content":"..."}]）' AFTER `cooperation`;

-- 修改image字段注释，并添加images字段用于存储多张图片
ALTER TABLE `culture_project` 
MODIFY COLUMN `image` VARCHAR(500) NULL COMMENT '项目封面图（单张，兼容旧数据）';

-- 添加图片列表字段（JSON字符串，存储多张图片）
ALTER TABLE `culture_project` 
ADD COLUMN `images` TEXT NULL COMMENT '项目图片列表（JSON数组，如：["url1","url2"]）' AFTER `image`;

-- 查看表结构确认
-- DESCRIBE `culture_project`;


