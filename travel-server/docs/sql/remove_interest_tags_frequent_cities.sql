-- ============================================
-- 删除 sys_user 表中的 interest_tags 和 frequent_cities 字段
-- 执行前请备份数据库
-- 说明：兴趣标签和常去城市信息已迁移到用户画像服务，从浏览历史、收藏记录等分析获取
-- ============================================

-- 1. 查看当前字段定义（可选，用于确认）
-- SHOW CREATE TABLE sys_user;
-- DESC sys_user;

-- 2. 删除 interest_tags 字段
-- 注意：MySQL 5.7 不支持 DROP COLUMN IF EXISTS，需要直接删除
-- 如果字段不存在会报错，可以忽略错误
ALTER TABLE sys_user 
DROP COLUMN interest_tags;

-- 3. 删除 frequent_cities 字段
ALTER TABLE sys_user 
DROP COLUMN frequent_cities;

-- MySQL 8.0.23+ 版本可以使用以下语法（如果上面的报错，可以尝试这个）：
-- ALTER TABLE sys_user DROP COLUMN IF EXISTS interest_tags;
-- ALTER TABLE sys_user DROP COLUMN IF EXISTS frequent_cities;

-- ============================================
-- 验证SQL执行结果（可选）
-- ============================================

-- 查看表结构，确认字段已删除
-- DESC sys_user;

-- 查看所有字段列表
-- SHOW COLUMNS FROM sys_user;









