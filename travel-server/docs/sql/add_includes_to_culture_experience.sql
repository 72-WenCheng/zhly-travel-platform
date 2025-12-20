-- 为文化体验表添加费用包含（includes）字段
-- 执行时间：2025-01-XX

ALTER TABLE `culture_experience` 
ADD COLUMN `includes` TEXT NULL COMMENT '费用包含（JSON数组，如：["全套材料工具","茶歇/软饮","场地与讲解"]）' 
AFTER `flow`;

-- 验证字段是否添加成功
-- SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT 
-- FROM INFORMATION_SCHEMA.COLUMNS 
-- WHERE TABLE_SCHEMA = DATABASE() 
--   AND TABLE_NAME = 'culture_experience' 
--   AND COLUMN_NAME = 'includes';




