-- ============================================
-- 为 search_log 表添加国家/地区字段
-- 说明：用于支持全球访问统计功能
-- ============================================

-- 使用存储过程安全地添加字段（如果不存在）
DELIMITER $$

DROP PROCEDURE IF EXISTS `add_country_fields_to_search_log`$$

CREATE PROCEDURE `add_country_fields_to_search_log`()
BEGIN
    DECLARE ip_address_exists INT DEFAULT 0;
    
    -- 检查 ip_address 字段是否存在
    SELECT COUNT(*) INTO ip_address_exists
    FROM information_schema.COLUMNS 
    WHERE TABLE_SCHEMA = DATABASE() 
    AND TABLE_NAME = 'search_log' 
    AND COLUMN_NAME = 'ip_address';
    
    -- 检查并添加 country_code 字段
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS 
        WHERE TABLE_SCHEMA = DATABASE() 
        AND TABLE_NAME = 'search_log' 
        AND COLUMN_NAME = 'country_code'
    ) THEN
        IF ip_address_exists > 0 THEN
            ALTER TABLE `search_log` 
            ADD COLUMN `country_code` VARCHAR(10) DEFAULT NULL COMMENT '国家/地区代码（ISO-2，例如 CN、US）' AFTER `ip_address`;
        ELSE
            ALTER TABLE `search_log` 
            ADD COLUMN `country_code` VARCHAR(10) DEFAULT NULL COMMENT '国家/地区代码（ISO-2，例如 CN、US）';
        END IF;
    END IF;
    
    -- 检查并添加 country_name 字段
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS 
        WHERE TABLE_SCHEMA = DATABASE() 
        AND TABLE_NAME = 'search_log' 
        AND COLUMN_NAME = 'country_name'
    ) THEN
        IF EXISTS (
            SELECT * FROM information_schema.COLUMNS 
            WHERE TABLE_SCHEMA = DATABASE() 
            AND TABLE_NAME = 'search_log' 
            AND COLUMN_NAME = 'country_code'
        ) THEN
            ALTER TABLE `search_log` 
            ADD COLUMN `country_name` VARCHAR(100) DEFAULT NULL COMMENT '国家/地区名称（中文）' AFTER `country_code`;
        ELSE
            ALTER TABLE `search_log` 
            ADD COLUMN `country_name` VARCHAR(100) DEFAULT NULL COMMENT '国家/地区名称（中文）';
        END IF;
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL `add_country_fields_to_search_log`();

-- 删除临时存储过程
DROP PROCEDURE IF EXISTS `add_country_fields_to_search_log`;

-- 验证表结构
-- DESC `search_log`;
-- SHOW CREATE TABLE `search_log`;

