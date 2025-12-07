package com.zhly.admin.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 管理端用户实体
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class AdminUser {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    // user_type 字段已移除，不再使用
    private Integer role;            // 用户角色：1-管理员, 2-普通用户
    private Integer travelPreference;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String lastLoginIp;
    private LocalDateTime lastLoginTime;
    private String avatar;
    // points 和 level 字段已从数据库表中移除，这些数据从 user_points 表获取
    // 保留字段定义以兼容旧代码，但不会从数据库查询这些字段
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer points;          // 积分（已移除，从 user_points 表获取）
    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private Integer level;           // 等级（已移除，从 user_points 表获取）
    // interest_tags 和 frequent_cities 字段已移除，这些数据从用户画像服务获取
    private Integer deleted;          // 逻辑删除：0-未删除，1-已删除
}









