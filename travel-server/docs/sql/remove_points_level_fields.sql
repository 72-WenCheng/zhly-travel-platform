-- ============================================
-- 删除 sys_user 表中的 points 和 level 字段
-- 执行前请备份数据库
-- 说明：积分和等级信息已迁移到 user_points 表，sys_user 表中的字段可以删除
-- ============================================

-- 1. 查看当前字段定义（可选，用于确认）
-- SHOW CREATE TABLE sys_user;
-- DESC sys_user;

-- 2. 删除 points 字段
-- 注意：MySQL 5.7 不支持 DROP COLUMN IF EXISTS，需要直接删除
-- 如果字段不存在会报错，可以忽略错误
ALTER TABLE sys_user 
DROP COLUMN points;

-- 3. 删除 level 字段
ALTER TABLE sys_user 
DROP COLUMN level;

-- MySQL 8.0.23+ 版本可以使用以下语法（如果上面的报错，可以尝试这个）：
-- ALTER TABLE sys_user DROP COLUMN IF EXISTS points;
-- ALTER TABLE sys_user DROP COLUMN IF EXISTS level;

-- ============================================
-- 验证SQL执行结果（可选）
-- ============================================

-- 查看表结构，确认字段已删除
-- DESC sys_user;

-- 查看所有字段列表
-- SHOW COLUMNS FROM sys_user;







