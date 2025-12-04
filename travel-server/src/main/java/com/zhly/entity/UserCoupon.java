package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体
 * 
 * @author zhly
 * @since 2025-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("user_coupon")
public class UserCoupon implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 优惠券ID
     */
    private Long couponId;
    
    /**
     * 优惠券名称（冗余字段，方便查询）
     */
    private String couponName;
    
    /**
     * 优惠券类型（1-满减券 2-折扣券 3-兑换券）
     */
    private Integer couponType;
    
    /**
     * 优惠金额/折扣值
     */
    private java.math.BigDecimal discountValue;
    
    /**
     * 最低使用金额
     */
    private java.math.BigDecimal minAmount;
    
    /**
     * 来源类型（1-等级权益自动发放 2-手动领取 3-活动赠送）
     */
    private Integer sourceType;
    
    /**
     * 来源描述
     */
    private String sourceDesc;
    
    /**
     * 状态（1-未使用 2-已使用 3-已过期 4-已作废）
     */
    private Integer status;
    
    /**
     * 有效期开始时间
     */
    private LocalDateTime validStartTime;
    
    /**
     * 有效期结束时间
     */
    private LocalDateTime validEndTime;
    
    /**
     * 使用时间
     */
    private LocalDateTime usedTime;
    
    /**
     * 使用的订单ID
     */
    private Long orderId;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}


