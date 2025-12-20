-- 为project_application表添加职务和合作意向字段
-- 执行时间：2025-12-20

-- 添加职务字段
ALTER TABLE `project_application` 
ADD COLUMN `position` VARCHAR(100) NULL COMMENT '职务' AFTER `contact_email`;

-- 添加合作意向字段
ALTER TABLE `project_application` 
ADD COLUMN `cooperation_type` VARCHAR(50) NULL COMMENT '合作意向（investment-资金投资, technology-技术支持, operation-运营管理, resource-资源对接, other-其他合作）' AFTER `position`;

-- 查看表结构确认
-- DESCRIBE `project_application`;

