package com.zhly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhly.entity.UserCoupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户优惠券Mapper
 * 
 * @author zhly
 * @since 2025-01-01
 */
@Mapper
public interface UserCouponMapper extends BaseMapper<UserCoupon> {
    
    /**
     * 查询用户指定月份的优惠券发放记录
     * @param userId 用户ID
     * @param sourceType 来源类型（1-等级权益自动发放）
     * @param year 年份
     * @param month 月份
     * @return 优惠券数量
     */
    @Select("SELECT COUNT(*) FROM user_coupon " +
            "WHERE user_id = #{userId} " +
            "AND source_type = #{sourceType} " +
            "AND YEAR(create_time) = #{year} " +
            "AND MONTH(create_time) = #{month}")
    Long countByUserAndMonth(@Param("userId") Long userId, 
                            @Param("sourceType") Integer sourceType,
                            @Param("year") Integer year,
                            @Param("month") Integer month);
    
    /**
     * 查询用户可用的优惠券列表
     * @param userId 用户ID
     * @return 优惠券列表
     */
    @Select("SELECT * FROM user_coupon " +
            "WHERE user_id = #{userId} " +
            "AND status = 1 " +
            "AND valid_end_time >= NOW() " +
            "ORDER BY valid_end_time ASC")
    List<UserCoupon> selectAvailableCoupons(@Param("userId") Long userId);
    
    /**
     * 查询用户指定等级的优惠券发放记录（用于检查是否已发放）
     * @param userId 用户ID
     * @param sourceDesc 来源描述（包含等级信息）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 优惠券数量
     */
    @Select("SELECT COUNT(*) FROM user_coupon " +
            "WHERE user_id = #{userId} " +
            "AND source_type = 1 " +
            "AND source_desc LIKE CONCAT('%', #{sourceDesc}, '%') " +
            "AND create_time >= #{startTime} " +
            "AND create_time <= #{endTime}")
    Long countByUserAndSourceDesc(@Param("userId") Long userId,
                                  @Param("sourceDesc") String sourceDesc,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);
}


