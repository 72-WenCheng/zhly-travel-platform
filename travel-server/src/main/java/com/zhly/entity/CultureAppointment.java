package com.zhly.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 文旅预约记录
 */
@Data
@TableName("culture_appointment")
public class CultureAppointment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String appointmentNo;
    private Long userId;
    /** 1服务 2民宿 3体验 4项目 */
    private Integer appointmentType;
    private Long itemId;
    private String itemName;
    private String itemImage;
    private LocalDate appointmentDate;
    private String timeSlot;
    private Integer peopleCount;
    private String contactName;
    private String contactPhone;
    private String remarks;
    /** 1待确认 2已确认 3已完成 4已取消 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
























