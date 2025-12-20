package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 文旅项目实体
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("culture_project")
public class CultureProject {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;
    private Integer type;
    private String region;
    private String location;
    private String description;
    private String image;
    private String images; // 图片列表（JSON数组）
    private java.math.BigDecimal price;
    private java.math.BigDecimal rating;
    private String contactPerson;
    private String contactPhone;
    private String email; // 联系邮箱
    private String address;
    private Integer status;
    private Integer viewCount;
    private Integer orderCount;
    private java.math.BigDecimal revenue;
    private String startDate; // 项目开始时间（年月，格式：YYYY-MM）
    private Integer beneficiaries; // 惠及农户数（户）
    private String tags; // 项目标签（JSON数组）
    private String goals; // 项目目标（JSON数组）
    private String cooperation; // 合作方式（JSON数组）
    private String policies; // 政策支持（JSON数组）
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

