package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.zhly.entity.CultureServicePackage;

/**
 * 农家乐/乡村服务实体
 */
@Data
@TableName("culture_service")
public class CultureService implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String title;
    private String location;
    private BigDecimal rating;
    private Integer views;
    private String description;
    /** JSON 数组字符串 */
    private String images;
    /** JSON 数组字符串 */
    private String features;
    /** JSON 数组，元素包含 name/icon */
    private String facilities;
    private String contactPhone;
    /** 0-关闭 1-正常 2-维护中 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private List<CultureServicePackage> packages;
}

