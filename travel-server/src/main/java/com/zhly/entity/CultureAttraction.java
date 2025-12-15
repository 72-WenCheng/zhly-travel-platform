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
    private String features;
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





