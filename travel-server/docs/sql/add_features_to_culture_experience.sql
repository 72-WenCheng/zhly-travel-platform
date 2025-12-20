-- 为文化体验表添加体验亮点（features）字段
-- 执行时间：2025-01-XX

ALTER TABLE `culture_experience` 
ADD COLUMN `features` TEXT NULL COMMENT '体验亮点/精选标签（JSON数组，格式：[{"emoji":"🧵","title":"师傅一对一指导","description":"非遗传承人现场教学，零基础也能上手"}]）' 
AFTER `slogan`;

-- 验证字段是否添加成功
-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
--   AND TABLE_NAME = 'culture_experience' 
--   AND COLUMN_NAME = 'features';




