package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 用户实体
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class User {
    
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("nickname")
    private String nickname;
    
    @TableField("avatar")
    private String avatar;
    
    @TableField("email")
    private String email;
    
    @TableField("phone")
    private String phone;
    
    @TableField("gender")
    private Integer gender;
    
    @TableField("age")
    private Integer age;
    
    // user_type 字段已移除，不再使用
    
    @TableField("role")
    private Integer role;          // 用户角色：1-管理员, 2-普通用户
    
    // 注意：level_id 和 points 字段已移除，等级和积分信息从 user_points 表获取
    
    @TableField("travel_preference")
    private Integer travelPreference;
    
    // interest_tags 和 frequent_cities 字段已移除，这些数据从用户画像服务获取
    
    @TableField("status")
    private Integer status;
    
    @TableField("login_count")
    private Integer loginCount;    // 登录次数
    
    @TableField("last_login_time")
    private LocalDateTime lastLoginTime;
    
    @TableField("last_login_ip")
    private String lastLoginIp;
    
    @TableField("create_time")
    private LocalDateTime createTime;
    
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    @TableField("deleted")
    private Integer deleted;
}
