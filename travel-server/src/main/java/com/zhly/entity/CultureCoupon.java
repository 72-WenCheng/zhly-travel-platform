package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 文旅优惠券
 */
@Data
@TableName("culture_coupon")
public class CultureCoupon implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String description;
    /** 1满减 2折扣 3直减 4包邮 */
    private Integer couponType;
    private BigDecimal discountAmount;
    private BigDecimal discountRate;
    private BigDecimal minSpend;
    private Integer totalCount;
    private Integer receivedCount;
    private Integer usedCount;
    private LocalDateTime validStart;
    private LocalDateTime validEnd;
    /** 0下架 1上架 */
    private Integer status;
    /** 适用范围 JSON */
    private String scope;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}





