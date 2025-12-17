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
 * 农家乐套餐
 */
@Data
@TableName("culture_service_package")
public class CultureServicePackage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long serviceId;
    private String name;
    private BigDecimal price;
    private String unit;
    private String description;
    /** JSON 数组，包含套餐包含项 */
    private String includes;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}



















