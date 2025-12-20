package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 文化体验实体
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("culture_experience")
public class CultureExperience {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 体验名称
     */
    private String name;
    
    /**
     * 分类名称
     */
    private String categoryName;
    
    /**
     * 位置
     */
    private String location;
    
    /**
     * 价格
     */
    private BigDecimal price;
    
    /**
     * 时长
     */
    private String duration;
    
    /**
     * 评分
     */
    private BigDecimal rating;
    
    /**
     * 状态：active-上架, inactive-下架
     */
    private String status;
    
    /**
     * 图片列表（JSON数组）
     */
    private String images;
    
    /**
     * 简介/标语（用于详情页展示，如：跟着非遗老师亲手完成一件蜀绣作品，收藏一段东方美学）
     */
    private String slogan;
    
    /**
     * 体验亮点/精选标签（JSON数组，格式：[{"emoji":"🧵","title":"师傅一对一指导","description":"非遗传承人现场教学，零基础也能上手"}]）
     */
    private String features;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 适合人群（如：亲子/团建/非遗爱好者）
     */
    private String suitableFor;
    
    /**
     * 流程（JSON数组）
     */
    private String flow;
    
    /**
     * 费用包含（JSON数组，如：["全套材料工具","茶歇/软饮","场地与讲解"]）
     */
    private String includes;
    
    /**
     * 注意事项/体验提示（JSON数组，如：["请提前15分钟到场","穿着舒适便于活动的衣物"]）
     */
    private String notes;
    
    /**
     * 咨询电话
     */
    private String contactPhone;
    
    /**
     * 教师介绍（JSON对象，格式：{"name":"林老师","title":"省级非遗传承人 · 蜀绣导师","avatar":"头像URL","bio":"从业20年，擅长将传统纹样与当代设计结合..."}）
     */
    private String host;
    
    /**
     * 评论（JSON数组）
     */
    private String reviews;
    
    /**
     * 浏览量
     */
    private Integer viewCount;
    
    /**
     * 预订量
     */
    private Integer orderCount;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}











































































