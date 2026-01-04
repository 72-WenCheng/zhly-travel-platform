package com.zhly.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhly.common.Result;
import com.zhly.entity.Coupon;
import com.zhly.entity.User;
import com.zhly.entity.UserCoupon;
import com.zhly.mapper.CouponMapper;
import com.zhly.mapper.UserCouponMapper;
import com.zhly.mapper.UserMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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
    private final UserMapper userMapper;

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

    @Operation(summary = "批量发放优惠券给全部用户")
    @PostMapping("/grant/all")
    public Result<String> grantCouponToAllUsers(@RequestBody Map<String, Object> body) {
        try {
            Long couponId = parseLong(body.get("couponId"));
            Integer customValidDays = parseInteger(body.get("validDays"));
            String sourceDesc = body.get("sourceDesc") != null ? body.get("sourceDesc").toString() : "管理端批量发放";

            if (couponId == null) {
                return Result.badRequest("couponId 不能为空");
            }

            Coupon coupon = couponMapper.selectById(couponId);
            if (coupon == null) {
                return Result.error("优惠券不存在");
            }
            if (coupon.getStatus() != null && coupon.getStatus() != 2) {
                return Result.error("优惠券未处于进行中状态，无法发放");
            }

            // 获取所有用户ID
            QueryWrapper<User> userWrapper = new QueryWrapper<>();
            userWrapper.select("id");
            List<User> users = userMapper.selectList(userWrapper);
            
            if (users == null || users.isEmpty()) {
                return Result.error("没有可发放的用户");
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

            int successCount = 0;
            int failCount = 0;
            int skipCount = 0;

            for (User user : users) {
                try {
                    // 检查优惠券余量
                    if (coupon.getRemainingCount() != null && coupon.getRemainingCount() <= 0) {
                        skipCount++;
                        continue;
                    }

                    // 检查用户是否已有该优惠券（可选，根据业务需求决定是否跳过）
                    QueryWrapper<UserCoupon> checkWrapper = new QueryWrapper<>();
                    checkWrapper.eq("user_id", user.getId())
                               .eq("coupon_id", couponId)
                               .eq("status", 1); // 未使用
                    Long existingCount = userCouponMapper.selectCount(checkWrapper);
                    if (existingCount > 0) {
                        skipCount++;
                        continue;
                    }

                    UserCoupon userCoupon = new UserCoupon();
                    userCoupon.setUserId(user.getId());
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
                    successCount++;

                    // 扣减剩余数量（如有设置）
                    if (coupon.getRemainingCount() != null && coupon.getRemainingCount() > 0) {
                        coupon.setRemainingCount(coupon.getRemainingCount() - 1);
                        coupon.setUpdateTime(now);
                        couponMapper.updateById(coupon);
                    }
                } catch (Exception e) {
                    failCount++;
                    // 继续处理下一个用户
                }
            }

            String message = String.format("批量发放完成：成功 %d 个，跳过 %d 个，失败 %d 个", successCount, skipCount, failCount);
            return Result.success(message);
        } catch (Exception e) {
            return Result.error("批量发放失败: " + e.getMessage());
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























