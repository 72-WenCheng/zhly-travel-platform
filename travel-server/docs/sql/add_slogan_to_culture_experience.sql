-- 为文化体验表添加简介（slogan）字段
-- 执行时间：2025-01-XX

ALTER TABLE `culture_experience` 
ADD COLUMN `slogan` VARCHAR(500) NULL COMMENT '简介/标语（用于详情页展示，如：跟着非遗老师亲手完成一件蜀绣作品，收藏一段东方美学）' 
AFTER `images`;

-- 验证字段是否添加成功
-- SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
--   AND TABLE_NAME = 'culture_experience' 
--   AND COLUMN_NAME = 'slogan';




