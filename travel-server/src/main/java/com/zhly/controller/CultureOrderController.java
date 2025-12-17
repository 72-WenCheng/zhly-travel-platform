package com.zhly.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureOrder;
import com.zhly.mapper.CultureOrderMapper;
import com.zhly.mapper.UserCouponMapper;
import com.zhly.entity.UserCoupon;
import com.zhly.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/culture/order")
@RequiredArgsConstructor
public class CultureOrderController {

    private final CultureOrderMapper cultureOrderMapper;
    private final UserCouponMapper userCouponMapper;
    private final SecurityUtils securityUtils;

    @PostMapping
    public Result<String> createOrder(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            CultureOrder order = new CultureOrder();
            order.setOrderNo(generateOrderNo());
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            order.setUserId(userId);
            order.setProductType(intVal(payload.get("productType"), 1));
            order.setProductId(longVal(payload.get("productId")));
            order.setProductName(str(payload.get("productName")));
            order.setProductImage(str(payload.get("productImage")));
            order.setSpecification(str(payload.get("specification")));
            order.setProductPrice(dec(payload.get("productPrice")));
            order.setQuantity(intVal(payload.get("quantity"), 1));
            order.setTotalAmount(dec(payload.get("totalAmount")));
            order.setFinalAmount(dec(payload.get("finalAmount")));

            Long couponId = longVal(payload.get("couponId"));
            BigDecimal couponDiscount = dec(payload.get("couponDiscount"));
            if (couponId != null) {
                UserCoupon userCoupon = userCouponMapper.selectById(couponId);
                if (userCoupon == null || !userId.equals(userCoupon.getUserId())) {
                    return Result.error("优惠券不可用");
                }
                // 校验有效期和状态
                if (userCoupon.getStatus() != null && userCoupon.getStatus() != 1) {
                    return Result.error("优惠券状态不可用");
                }
                if (userCoupon.getValidEndTime() != null && userCoupon.getValidEndTime().isBefore(LocalDateTime.now())) {
                    return Result.error("优惠券已过期");
                }
                if (userCoupon.getMinAmount() != null && order.getTotalAmount() != null
                        && order.getTotalAmount().compareTo(userCoupon.getMinAmount()) < 0) {
                    return Result.error("未达到优惠券使用门槛");
                }
                order.setCouponId(couponId);
                order.setCouponDiscount(couponDiscount != null ? couponDiscount : BigDecimal.ZERO);

                // 占用优惠券（锁定）
                userCoupon.setStatus(4); // 4-锁定
                userCoupon.setUpdateTime(LocalDateTime.now());
                userCouponMapper.updateById(userCoupon);
            }
            order.setContactName(str(payload.get("contactName")));
            order.setContactPhone(str(payload.get("contactPhone")));
            order.setAddressId(longVal(payload.get("addressId")));
            order.setDetailedAddress(str(payload.get("address")));
            order.setBuyerMessage(str(payload.get("buyerMessage")));
            order.setOrderStatus(1); // 待支付
            order.setPaymentMethod(str(payload.get("paymentMethod")));
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            cultureOrderMapper.insert(order);
            return Result.success(order.getOrderNo());
        } catch (Exception e) {
            return Result.error("下单失败: " + e.getMessage());
        }
    }

    /**
    * 模拟支付成功：更新订单状态与券状态
    */
    @PutMapping("/{orderNo}/pay")
    public Result<String> paySuccess(@PathVariable String orderNo, HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            CultureOrder order = cultureOrderMapper.selectOne(
                    new LambdaQueryWrapper<CultureOrder>()
                            .eq(CultureOrder::getOrderNo, orderNo)
                            .eq(CultureOrder::getUserId, userId)
            );
            if (order == null) {
                return Result.error("订单不存在");
            }
            if (order.getOrderStatus() != null && order.getOrderStatus() != 1) {
                return Result.error("订单状态不可支付");
            }

            // 更新订单状态为已支付
            order.setOrderStatus(2); // 已支付
            order.setPaymentTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            cultureOrderMapper.updateById(order);

            // 如果有优惠券，标记为已使用
            if (order.getCouponId() != null) {
                UserCoupon userCoupon = userCouponMapper.selectById(order.getCouponId());
                if (userCoupon != null && userId.equals(userCoupon.getUserId())) {
                    userCoupon.setStatus(2); // 已使用
                    userCoupon.setUsedTime(LocalDateTime.now());
                    userCouponMapper.updateById(userCoupon);
                }
            }

            return Result.success("支付成功");
        } catch (Exception e) {
            return Result.error("支付失败: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<Page<CultureOrder>> myOrders(@RequestParam(defaultValue = "1") int page,
                                               @RequestParam(defaultValue = "10") int size,
                                               HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            Page<CultureOrder> p = new Page<>(page, size);
            Page<CultureOrder> result = cultureOrderMapper.selectPage(p,
                    new LambdaQueryWrapper<CultureOrder>()
                            .eq(CultureOrder::getUserId, userId)
                            .orderByDesc(CultureOrder::getCreateTime));
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 用户取消订单
     */
    @PutMapping("/{id}/cancel")
    public Result<String> cancelOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            CultureOrder order = cultureOrderMapper.selectById(id);
            if (order == null || !userId.equals(order.getUserId())) {
                return Result.error("订单不存在");
            }
            // 只有待支付、已支付的订单允许取消
            if (order.getOrderStatus() != null &&
                order.getOrderStatus() != 1 &&
                order.getOrderStatus() != 2) {
                return Result.error("当前状态不允许取消订单");
            }
            order.setOrderStatus(5); // 已取消
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            cultureOrderMapper.updateById(order);
            return Result.success("订单已取消");
        } catch (Exception e) {
            return Result.error("取消订单失败: " + e.getMessage());
        }
    }

    /**
     * 用户确认收货
     */
    @PutMapping("/{id}/confirm")
    public Result<String> confirmOrder(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            CultureOrder order = cultureOrderMapper.selectById(id);
            if (order == null || !userId.equals(order.getUserId())) {
                return Result.error("订单不存在");
            }
            if (order.getOrderStatus() == null || order.getOrderStatus() != 3) {
                return Result.error("当前状态不允许确认收货");
            }
            order.setOrderStatus(4); // 已完成
            order.setFinishTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            cultureOrderMapper.updateById(order);
            return Result.success("确认收货成功");
        } catch (Exception e) {
            return Result.error("确认收货失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户的单个订单详情
     */
    @GetMapping("/{id}")
    public Result<CultureOrder> getOrderDetail(@PathVariable Long id, HttpServletRequest request) {
        try {
            Long userId = securityUtils.getLoginUserId(request);
            if (userId == null) {
                return Result.unauthorized("未登录或Token无效");
            }
            CultureOrder order = cultureOrderMapper.selectById(id);
            if (order == null || !userId.equals(order.getUserId())) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            return Result.error("查询订单详情失败: " + e.getMessage());
        }
    }

    private String generateOrderNo() {
        return "CO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String str(Object o) {
        return o == null ? null : o.toString();
    }

    private Integer intVal(Object o, Integer def) {
        try {
            return o == null ? def : Integer.valueOf(o.toString());
        } catch (Exception e) {
            return def;
        }
    }

    private Long longVal(Object o) {
        try {
            return o == null ? null : Long.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private BigDecimal dec(Object o) {
        try {
            return o == null ? null : new BigDecimal(o.toString());
        } catch (Exception e) {
            return null;
        }
    }
}

