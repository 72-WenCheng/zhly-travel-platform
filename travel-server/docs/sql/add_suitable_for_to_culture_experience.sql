-- 为文化体验表添加适合人群（suitableFor）字段
-- 执行时间：2025-01-XX

ALTER TABLE `culture_experience` 
ADD COLUMN `suitable_for` VARCHAR(100) NULL COMMENT '适合人群（如：亲子/团建/非遗爱好者）' 
AFTER `description`;

-- 验证字段是否添加成功
-- SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
--   AND TABLE_NAME = 'culture_experience' 
--   AND COLUMN_NAME = 'suitable_for';




