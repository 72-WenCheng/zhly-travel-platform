-- ============================================
-- 为 search_log 表添加 IP 地址字段
-- 说明：用于支持地理统计功能
-- ============================================

-- 使用存储过程安全地添加字段（如果不存在）
DELIMITER $$

DROP PROCEDURE IF EXISTS `add_ip_address_to_search_log`$$

CREATE PROCEDURE `add_ip_address_to_search_log`()
BEGIN
    -- 检查并添加 ip_address 字段
    IF NOT EXISTS (
        SELECT * FROM information_schema.COLUMNS 
        WHERE TABLE_SCHEMA = DATABASE() 
        AND TABLE_NAME = 'search_log' 
        AND COLUMN_NAME = 'ip_address'
    ) THEN
        ALTER TABLE `search_log` 
        ADD COLUMN `ip_address` VARCHAR(50) DEFAULT NULL COMMENT '访问者IP';
    END IF;
END$$

DELIMITER ;

-- 执行存储过程
CALL `add_ip_address_to_search_log`();

-- 删除临时存储过程
DROP PROCEDURE IF EXISTS `add_ip_address_to_search_log`;

-- 验证表结构
-- DESC `search_log`;

