-- 去掉个人资料出行字段(travel_preference)的默认值
-- 执行时间：2024-01-01

-- 查看当前字段定义
-- SHOW CREATE TABLE sys_user;

-- 修改 travel_preference 字段，去掉默认值
ALTER TABLE sys_user 
MODIFY COLUMN travel_preference INT NULL COMMENT '出行偏好：1-个人, 2-情侣, 3-家庭, 4-团队, 5-商务, 6-其他';

-- 验证修改结果（可选）
-- DESC sys_user;



