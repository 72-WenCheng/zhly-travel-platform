package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品服务实体
 * 
 * @author zhly
 * @since 2025-10-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("culture_product")
public class CultureProduct implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long merchantId;
    private String productName;
    /**
     * 类型（1-住宿，2-餐饮，3-门票，4-特产，5-体验项目，6-演出）
     */
    private Integer productType;

    /** 分类，如：茶叶、粮油等 */
    private String category;
    private String origin;
    private String unit;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;  // -1表示无限
    private String description;
    /** 简要介绍 / 卖点摘要 */
    private String summary;
    /** 保质期，如：12个月 */
    private String shelfLife;
    /** 规格参数（JSON 数组或文本） */
    private String specifications;
    /** 商品图片，JSON 数组 */
    private String images;
    private String coverImage;
    private String features;
    private String highlights;
    /** 规格选择，JSON 数组，如 [{\"label\":\"250g 礼盒装\",\"value\":\"250g\"}] */
    private String availableSpecs;
    private String badge;
    private BigDecimal rating;
    private Integer reviewCount;
    private BigDecimal commissionRate;
    private Integer salesCount;
    private Integer status;  // 0-下架，1-上架，2-售罄/维护
    private String contactPhone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}







