-- 修复start_date字段类型问题
-- 如果start_date字段是DATE类型，需要修改为VARCHAR(7)以支持"YYYY-MM"格式
-- 执行时间：2024-01-01

-- 检查字段类型（执行前可以先查看）
-- SHOW COLUMNS FROM `culture_project` LIKE 'start_date';

-- 方法1：如果字段为空或可以清空，直接修改类型
-- ALTER TABLE `culture_project` 
-- MODIFY COLUMN `start_date` VARCHAR(7) NULL COMMENT '项目开始时间（年月，格式：YYYY-MM）';

-- 方法2：如果字段中有DATE格式的数据，需要先转换（推荐）
-- 步骤1：添加临时字段
ALTER TABLE `culture_project` 
ADD COLUMN `start_date_temp` VARCHAR(7) NULL COMMENT '临时字段：项目开始时间（年月）';

-- 步骤2：转换现有数据（如果有DATE格式的数据，转换为YYYY-MM格式）
UPDATE `culture_project` 
SET `start_date_temp` = DATE_FORMAT(`start_date`, '%Y-%m') 
WHERE `start_date` IS NOT NULL;

-- 步骤3：删除原字段
ALTER TABLE `culture_project` DROP COLUMN `start_date`;

-- 步骤4：重命名临时字段
ALTER TABLE `culture_project` 
CHANGE COLUMN `start_date_temp` `start_date` VARCHAR(7) NULL COMMENT '项目开始时间（年月，格式：YYYY-MM）';

-- 验证修改结果
-- SHOW COLUMNS FROM `culture_project` LIKE 'start_date';


