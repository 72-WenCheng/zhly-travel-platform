-- ============================================
-- 创建用户优惠券表
-- 说明：用于存储用户领取的优惠券信息
-- ============================================

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










