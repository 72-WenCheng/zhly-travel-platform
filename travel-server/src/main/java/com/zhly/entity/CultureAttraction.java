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
 * 特色周边/景点
 */
@Data
@TableName("culture_attraction")
public class CultureAttraction implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;
    private String province;
    private String city;
    private Integer type;
    private String image;
    private String images;
    private String coverImage;
    private String description;
    /**
     * 自定义标签（例如 地理标志、特色产品），用于前台角标显示
     */
    private String badge;
    /**
     * 是否官方认证 0-否 1-是
     */
    private Integer certified;
    private BigDecimal rating;
    private BigDecimal score;
    private BigDecimal ticketPrice;
    private String address;
    private String phone;
    private String website;
    private String openTime;
    private String openingHours;
    private Integer suggestedDuration;
    private String bestSeason;
    private String tags;
    /**
     * 产品特点，多条以换行或分隔符存储
     */
    private String features;
    /**
     * 规格参数，前端以 JSON 字符串或多行「名称:值」形式存储
     */
    private String specifications;
    /**
     * 购买规格配置，JSON 数组，如 [{label:'250g 礼盒装', value:'250g', price:128}]
     */
    private String sellSpecs;
    private String transportation;
    private String nearbyFood;
    private String nearbyHotel;
    private String notes;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer status;
    private Integer viewCount;
    private Integer collectCount;
    private Integer deleted;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}



















