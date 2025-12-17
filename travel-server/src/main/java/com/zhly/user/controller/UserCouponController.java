package com.zhly.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.R;
import com.zhly.entity.UserCoupon;
import com.zhly.mapper.UserCouponMapper;
import com.zhly.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户端-优惠券Controller
 * 
 * @author zhly
 * @since 2025-01-01
 */
@Tag(name = "用户端-优惠券管理")
@RestController
@RequestMapping("/api/user/coupon")
@lombok.RequiredArgsConstructor
public class UserCouponController {
    
    private final UserCouponMapper userCouponMapper;
    private final SecurityUtils securityUtils;
    
    /**
     * 获取我的优惠券列表
     */
    @Operation(summary = "获取我的优惠券列表")
    @GetMapping("/list")
    public R<Map<String, Object>> getMyCoupons(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return R.unauthorized("未登录或Token无效");
            }
            Page<UserCoupon> pageObj = new Page<>(page, size);
            QueryWrapper<UserCoupon> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            
            if (status != null) {
                wrapper.eq("status", status);
            }
            
            wrapper.orderByDesc("create_time");
            userCouponMapper.selectPage(pageObj, wrapper);
            
            // 更新过期状态
            updateExpiredCoupons(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", pageObj.getRecords());
            result.put("total", pageObj.getTotal());
            result.put("page", page);
            result.put("size", size);
            
            return R.ok(result);
        } catch (Exception e) {
            return R.error("获取优惠券列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取可用优惠券列表（用于订单页面选择）
     */
    @Operation(summary = "获取可用优惠券列表")
    @GetMapping("/available")
    public R<Map<String, Object>> getAvailableCoupons(
            @RequestParam(required = false) java.math.BigDecimal orderAmount,
            HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return R.unauthorized("未登录或Token无效");
            }
            QueryWrapper<UserCoupon> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId)
                   .eq("status", 1) // 1-未使用
                   .ge("valid_end_time", LocalDateTime.now()); // 未过期
            
            // 此处不再按订单金额过滤，让前端根据 orderAmount 标记可用/不可用
            // 这样在“选择优惠券”弹窗里可以看到所有未使用且未过期的券
            wrapper.orderByAsc("valid_end_time"); // 按有效期升序，优先显示快过期的
            java.util.List<UserCoupon> coupons = userCouponMapper.selectList(wrapper);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", coupons);
            result.put("count", coupons.size());
            
            return R.ok(result);
        } catch (Exception e) {
            return R.error("获取可用优惠券失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取优惠券统计信息
     */
    @Operation(summary = "获取优惠券统计信息")
    @GetMapping("/stats")
    public R<Map<String, Object>> getCouponStats(HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return R.unauthorized("未登录或Token无效");
            }
            // 更新过期状态
            updateExpiredCoupons(userId);
            
            QueryWrapper<UserCoupon> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            
            // 统计可用
            QueryWrapper<UserCoupon> availableWrapper = new QueryWrapper<>();
            availableWrapper.eq("user_id", userId)
                           .eq("status", 1)
                           .ge("valid_end_time", LocalDateTime.now());
            Long availableCount = userCouponMapper.selectCount(availableWrapper);
            
            // 统计已使用
            QueryWrapper<UserCoupon> usedWrapper = new QueryWrapper<>();
            usedWrapper.eq("user_id", userId).eq("status", 2);
            Long usedCount = userCouponMapper.selectCount(usedWrapper);
            
            // 统计已过期
            QueryWrapper<UserCoupon> expiredWrapper = new QueryWrapper<>();
            expiredWrapper.eq("user_id", userId)
                         .and(w -> w.eq("status", 3).or().lt("valid_end_time", LocalDateTime.now()));
            Long expiredCount = userCouponMapper.selectCount(expiredWrapper);
            
            Map<String, Object> result = new HashMap<>();
            result.put("available", availableCount != null ? availableCount.intValue() : 0);
            result.put("used", usedCount != null ? usedCount.intValue() : 0);
            result.put("expired", expiredCount != null ? expiredCount.intValue() : 0);
            result.put("total", (availableCount != null ? availableCount.intValue() : 0) +
                               (usedCount != null ? usedCount.intValue() : 0) +
                               (expiredCount != null ? expiredCount.intValue() : 0));
            
            return R.ok(result);
        } catch (Exception e) {
            return R.error("获取优惠券统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新过期优惠券状态
     */
    private void updateExpiredCoupons(Long userId) {
        try {
            QueryWrapper<UserCoupon> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId)
                   .eq("status", 1) // 只更新未使用的
                   .lt("valid_end_time", LocalDateTime.now()); // 已过期
            
            java.util.List<UserCoupon> expiredCoupons = userCouponMapper.selectList(wrapper);
            for (UserCoupon coupon : expiredCoupons) {
                coupon.setStatus(3); // 3-已过期
                userCouponMapper.updateById(coupon);
            }
        } catch (Exception e) {
            System.err.println("更新过期优惠券状态失败: " + e.getMessage());
        }
    }
}










