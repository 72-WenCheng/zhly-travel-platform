-- ============================================
-- 修复用户优惠券表结构
-- 说明：如果表已存在但字段缺失，使用此脚本修复
-- ============================================

-- 1. 检查表是否存在，如果不存在则创建
CREATE TABLE IF NOT EXISTS `user_coupon` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
  `coupon_id` BIGINT(20) NOT NULL COMMENT '优惠券ID',
  `coupon_name` VARCHAR(100) DEFAULT NULL COMMENT '优惠券名称（冗余字段）',
  `coupon_type` INT(11) DEFAULT NULL COMMENT '优惠券类型（1-满减券 2-折扣券 3-兑换券）',
  `discount_value` DECIMAL(10,2) DEFAULT NULL COMMENT '优惠金额/折扣值',
  `min_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '最低使用金额',
  `source_type` INT(11) DEFAULT 1 COMMENT '来源类型（1-等级权益自动发放 2-手动领取 3-活动赠送）',
  `source_desc` VARCHAR(200) DEFAULT NULL COMMENT '来源描述',
  `status` INT(11) DEFAULT 1 COMMENT '状态（1-未使用 2-已使用 3-已过期 4-已作废）',
  `valid_start_time` DATETIME DEFAULT NULL COMMENT '有效期开始时间',
  `valid_end_time` DATETIME DEFAULT NULL COMMENT '有效期结束时间',
  `used_time` DATETIME DEFAULT NULL COMMENT '使用时间',
  `order_id` BIGINT(20) DEFAULT NULL COMMENT '使用的订单ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_coupon_id` (`coupon_id`),
  KEY `idx_status` (`status`),
  KEY `idx_valid_end_time` (`valid_end_time`),
  KEY `idx_user_status` (`user_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';

-- 2. 如果表已存在，检查并添加缺失的字段
-- 注意：MySQL 5.7 不支持 IF NOT EXISTS，需要手动检查

-- 添加 coupon_name 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `coupon_name` VARCHAR(100) DEFAULT NULL COMMENT '优惠券名称（冗余字段）' AFTER `coupon_id`;

-- 添加 coupon_type 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `coupon_type` INT(11) DEFAULT NULL COMMENT '优惠券类型（1-满减券 2-折扣券 3-兑换券）' AFTER `coupon_name`;

-- 添加 discount_value 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `discount_value` DECIMAL(10,2) DEFAULT NULL COMMENT '优惠金额/折扣值' AFTER `coupon_type`;

-- 添加 min_amount 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `min_amount` DECIMAL(10,2) DEFAULT NULL COMMENT '最低使用金额' AFTER `discount_value`;

-- 添加 source_type 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `source_type` INT(11) DEFAULT 1 COMMENT '来源类型（1-等级权益自动发放 2-手动领取 3-活动赠送）' AFTER `min_amount`;

-- 添加 source_desc 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `source_desc` VARCHAR(200) DEFAULT NULL COMMENT '来源描述' AFTER `source_type`;

-- 添加 status 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `status` INT(11) DEFAULT 1 COMMENT '状态（1-未使用 2-已使用 3-已过期 4-已作废）' AFTER `source_desc`;

-- 添加 valid_start_time 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `valid_start_time` DATETIME DEFAULT NULL COMMENT '有效期开始时间' AFTER `status`;

-- 添加 valid_end_time 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `valid_end_time` DATETIME DEFAULT NULL COMMENT '有效期结束时间' AFTER `valid_start_time`;

-- 添加 used_time 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `used_time` DATETIME DEFAULT NULL COMMENT '使用时间' AFTER `valid_end_time`;

-- 添加 order_id 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `order_id` BIGINT(20) DEFAULT NULL COMMENT '使用的订单ID' AFTER `used_time`;

-- 添加 create_time 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER `order_id`;

-- 添加 update_time 字段（如果不存在）
-- ALTER TABLE `user_coupon` ADD COLUMN `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间' AFTER `create_time`;

-- 3. 验证表结构
-- DESC `user_coupon`;
-- SHOW CREATE TABLE `user_coupon`;









