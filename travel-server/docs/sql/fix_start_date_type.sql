-- 修改start_date字段类型为VARCHAR（如果之前是DATE类型）
-- 执行时间：2024-01-01

-- 如果start_date字段是DATE类型，需要先修改为VARCHAR
-- 注意：如果字段中已有数据，需要先备份或转换数据格式

-- 方法1：如果字段为空或可以清空，直接修改类型
ALTER TABLE `culture_project` 
MODIFY COLUMN `start_date` VARCHAR(7) NULL COMMENT '项目开始时间（年月，格式：YYYY-MM）';

-- 方法2：如果字段中有DATE格式的数据，需要先转换
-- 先添加临时字段
-- ALTER TABLE `culture_project` ADD COLUMN `start_date_temp` VARCHAR(7) NULL;
-- 转换数据：将DATE格式转换为YYYY-MM格式
-- UPDATE `culture_project` SET `start_date_temp` = DATE_FORMAT(`start_date`, '%Y-%m') WHERE `start_date` IS NOT NULL;
-- 删除原字段
-- ALTER TABLE `culture_project` DROP COLUMN `start_date`;
-- 重命名临时字段
-- ALTER TABLE `culture_project` CHANGE COLUMN `start_date_temp` `start_date` VARCHAR(7) NULL COMMENT '项目开始时间（年月，格式：YYYY-MM）';


