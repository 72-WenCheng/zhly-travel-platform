-- 为文化体验表添加咨询电话和教师介绍字段
-- 执行时间：2025-01-XX

ALTER TABLE `culture_experience` 
ADD COLUMN `contact_phone` VARCHAR(20) NULL COMMENT '咨询电话' 
AFTER `notes`;

ALTER TABLE `culture_experience` 
ADD COLUMN `host` TEXT NULL COMMENT '教师介绍（JSON对象，格式：{"name":"林老师","title":"省级非遗传承人 · 蜀绣导师","avatar":"头像URL","bio":"从业20年，擅长将传统纹样与当代设计结合..."}）' 
AFTER `contact_phone`;

-- 验证字段是否添加成功
-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
--   AND TABLE_NAME = 'culture_experience' 
--   AND COLUMN_NAME IN ('contact_phone', 'host');




