package com.zhly.admin.controller;

import com.zhly.common.Result;
import com.zhly.entity.Coupon;
import com.zhly.entity.UserCoupon;
import com.zhly.mapper.CouponMapper;
import com.zhly.mapper.UserCouponMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 管理端-用户优惠券发放
 */
@Tag(name = "管理端-用户优惠券发放")
@RestController
@RequestMapping("/api/admin/user/coupon")
@RequiredArgsConstructor
public class AdminUserCouponController {

    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;

    @Operation(summary = "手动发放优惠券给指定用户")
    @PostMapping("/grant")
    public Result<String> grantCouponToUser(@RequestBody Map<String, Object> body) {
        try {
            Long userId = parseLong(body.get("userId"));
            Long couponId = parseLong(body.get("couponId"));
            Integer customValidDays = parseInteger(body.get("validDays"));
            String sourceDesc = body.get("sourceDesc") != null ? body.get("sourceDesc").toString() : "管理端发放";

            if (userId == null || couponId == null) {
                return Result.badRequest("userId 与 couponId 不能为空");
            }

            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                return Result.error("优惠券不存在");
            }
            if (coupon.getStatus() != null && coupon.getStatus() != 2) {
                return Result.error("优惠券未处于进行中状态，无法发放");
            }
            if (coupon.getRemainingCount() != null && coupon.getRemainingCount() <= 0) {
                return Result.error("优惠券余量不足");
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime validEndTime;
            if (customValidDays != null && customValidDays > 0) {
                validEndTime = now.plusDays(customValidDays);
            } else if (coupon.getValidDays() != null && coupon.getValidDays() > 0) {
                validEndTime = now.plusDays(coupon.getValidDays());
            } else if (coupon.getEndTime() != null) {
                validEndTime = coupon.getEndTime();
            } else {
                validEndTime = now.plusDays(30);
            }

            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(coupon.getId());
            userCoupon.setCouponName(coupon.getName());
            userCoupon.setCouponType(coupon.getType());
            userCoupon.setDiscountValue(coupon.getDiscountValue());
            userCoupon.setMinAmount(coupon.getMinAmount());
            userCoupon.setSourceType(2); // 2-手动发放
            userCoupon.setSourceDesc(sourceDesc);
            userCoupon.setStatus(1); // 未使用
            userCoupon.setValidStartTime(now);
            userCoupon.setValidEndTime(validEndTime);
            userCoupon.setCreateTime(now);
            userCoupon.setUpdateTime(now);

            userCouponMapper.insert(userCoupon);

            // 扣减剩余数量（如有设置）
            if (coupon.getRemainingCount() != null && coupon.getRemainingCount() > 0) {
                coupon.setRemainingCount(coupon.getRemainingCount() - 1);
                coupon.setUpdateTime(now);
                couponMapper.updateById(coupon);
            }

            return Result.success("发放成功");
        } catch (Exception e) {
            return Result.error("发放失败: " + e.getMessage());
        }
    }

    private Long parseLong(Object o) {
        try {
            return o == null ? null : Long.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer parseInteger(Object o) {
        try {
            return o == null ? null : Integer.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
}





















