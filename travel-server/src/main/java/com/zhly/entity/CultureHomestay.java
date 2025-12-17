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
 * 特色民宿
 */
@Data
@TableName("culture_homestay")
public class CultureHomestay implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String location;
    private BigDecimal rating;
    private Integer views;
    private BigDecimal price;
    private String roomType;
    private Integer capacity;
    private String description;
    private String images;
    private String features;
    private String amenities;
    private String highlightTags;
    private String highlights;
    private String contactPhone;
    private String cover;
    /** 0-关闭 1-正常 2-维护中 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}



















