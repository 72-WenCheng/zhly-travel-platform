package com.zhly.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.Coupon;
import com.zhly.mapper.CouponMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理端-优惠券管理控制器
 */
@Tag(name = "管理端-优惠券管理")
@RestController
@RequestMapping("/api/admin/culture/coupon")
@RequiredArgsConstructor
public class AdminCouponController {
    
    private final CouponMapper couponMapper;
    
    @Operation(summary = "分页查询优惠券")
    @GetMapping("/page")
    public Result<Page<Coupon>> pageCoupons(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Coupon> pageParam = new Page<>(page, size);
            QueryWrapper<Coupon> wrapper = new QueryWrapper<>();
            if (name != null && !name.isEmpty()) {
                wrapper.like("name", name);
            }
            if (type != null) {
                wrapper.eq("type", type);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            wrapper.orderByDesc("create_time");
            Page<Coupon> result = couponMapper.selectPage(pageParam, wrapper);
            
            // 自动更新状态（只更新自动状态，不更新已下架状态）
            LocalDateTime now = LocalDateTime.now();
            for (Coupon coupon : result.getRecords()) {
                // 只更新自动状态（1-未开始、2-进行中、3-已结束），保留手动下架状态（4）
                if (coupon.getStatus() != null && coupon.getStatus() >= 1 && coupon.getStatus() <= 3) {
                    Integer calculatedStatus = calculateStatusByTime(coupon, now);
                    if (calculatedStatus != null && !calculatedStatus.equals(coupon.getStatus())) {
                        coupon.setStatus(calculatedStatus);
                        coupon.setUpdateTime(now);
                        couponMapper.updateById(coupon);
                    }
                }
            }
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询优惠券失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取优惠券详情")
    @GetMapping("/{id}")
    public Result<Coupon> getCoupon(@PathVariable Long id) {
        try {
            Coupon coupon = couponMapper.selectById(id);
            if (coupon == null) {
                return Result.error("优惠券不存在");
            }
            return Result.success(coupon);
        } catch (Exception e) {
            return Result.error("获取优惠券详情失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "新增优惠券")
    @PostMapping
    public Result<String> createCoupon(@RequestBody Coupon coupon) {
        try {
            coupon.setRemainingCount(coupon.getTotalCount());
            coupon.setCreateTime(LocalDateTime.now());
            coupon.setUpdateTime(LocalDateTime.now());
            // 根据时间范围自动计算状态（如果未手动设置或状态为自动状态）
            calculateCouponStatus(coupon);
            couponMapper.insert(coupon);
            return Result.success("新增成功");
        } catch (Exception e) {
            return Result.error("新增失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "更新优惠券")
    @PutMapping("/{id}")
    public Result<String> updateCoupon(@PathVariable Long id, @RequestBody Coupon coupon) {
        try {
            Coupon existing = couponMapper.selectById(id);
            if (existing == null) {
                return Result.error("优惠券不存在");
            }
            
            coupon.setId(id);
            coupon.setUpdateTime(LocalDateTime.now());
            // 保持剩余数量不变
            coupon.setRemainingCount(existing.getRemainingCount());
            // 如果状态是自动状态（1-未开始、2-进行中、3-已结束），根据时间范围自动计算
            // 如果状态是4-已下架，则保留用户设置的值
            if (coupon.getStatus() == null || (coupon.getStatus() >= 1 && coupon.getStatus() <= 3)) {
                calculateCouponStatus(coupon);
            }
            couponMapper.updateById(coupon);
            return Result.success("更新成功");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{id}")
    public Result<String> deleteCoupon(@PathVariable Long id) {
        try {
            Coupon coupon = couponMapper.selectById(id);
            if (coupon == null) {
                return Result.error("优惠券不存在");
            }
            couponMapper.deleteById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "切换优惠券状态")
    @PutMapping("/{id}/status")
    public Result<String> toggleCouponStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusInfo) {
        try {
            Coupon coupon = couponMapper.selectById(id);
            if (coupon == null) {
                return Result.error("优惠券不存在");
            }
            
            coupon.setStatus(statusInfo.get("status"));
            coupon.setUpdateTime(LocalDateTime.now());
            couponMapper.updateById(coupon);
            return Result.success("操作成功");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据时间范围自动计算优惠券状态
     * 1-未开始：当前时间 < startTime
     * 2-进行中：startTime <= 当前时间 <= endTime
     * 3-已结束：当前时间 > endTime
     */
    private void calculateCouponStatus(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        Integer calculatedStatus = calculateStatusByTime(coupon, now);
        if (calculatedStatus != null) {
            coupon.setStatus(calculatedStatus);
        } else if (coupon.getValidDays() != null && coupon.getValidDays() > 0) {
            // 如果是固定天数类型，默认状态为进行中（领取后生效）
            if (coupon.getStatus() == null) {
                coupon.setStatus(2);
            }
        } else {
            // 如果没有时间信息，默认状态为未开始
            if (coupon.getStatus() == null) {
                coupon.setStatus(1);
            }
        }
    }
    
    /**
     * 根据时间计算状态值
     * @return 状态值，如果无法计算则返回null
     */
    private Integer calculateStatusByTime(Coupon coupon, LocalDateTime now) {
        LocalDateTime startTime = coupon.getStartTime();
        LocalDateTime endTime = coupon.getEndTime();
        
        // 如果有时间范围，根据时间计算状态
        if (startTime != null && endTime != null) {
            if (now.isBefore(startTime)) {
                // 当前时间在开始时间之前，状态为未开始
                return 1;
            } else if (now.isAfter(endTime)) {
                // 当前时间在结束时间之后，状态为已结束
                return 3;
            } else {
                // 当前时间在有效期内，状态为进行中
                return 2;
            }
        }
        return null;
    }
}

