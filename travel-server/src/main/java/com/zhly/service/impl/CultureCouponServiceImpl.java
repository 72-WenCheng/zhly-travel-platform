package com.zhly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhly.entity.CultureCoupon;
import com.zhly.mapper.CultureCouponMapper;
import com.zhly.service.CultureCouponService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CultureCouponServiceImpl extends ServiceImpl<CultureCouponMapper, CultureCoupon> implements CultureCouponService {

    @Override
    public Page<CultureCoupon> pageList(int page, int size, String name, Integer type, Integer status) {
        Page<CultureCoupon> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CultureCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(name != null && !name.isEmpty(), CultureCoupon::getTitle, name)
               .eq(type != null, CultureCoupon::getCouponType, type)
               .eq(status != null, CultureCoupon::getStatus, status)
               .orderByDesc(CultureCoupon::getUpdateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        CultureCoupon coupon = this.getById(id);
        if (coupon == null) return false;
        coupon.setStatus(status);
        return this.updateById(coupon);
    }

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("notStarted", this.count(new LambdaQueryWrapper<CultureCoupon>()
                .eq(CultureCoupon::getStatus, 1)
                .gt(CultureCoupon::getValidStart, LocalDateTime.now())));
        map.put("active", this.count(new LambdaQueryWrapper<CultureCoupon>()
                .eq(CultureCoupon::getStatus, 1)
                .le(CultureCoupon::getValidStart, LocalDateTime.now())
                .ge(CultureCoupon::getValidEnd, LocalDateTime.now())));
        map.put("totalIssued", this.list().stream()
                .mapToInt(c -> c.getTotalCount() == null ? 0 : c.getTotalCount())
                .sum());
        map.put("totalUsed", this.list().stream()
                .mapToInt(c -> c.getUsedCount() == null ? 0 : c.getUsedCount())
                .sum());
        return map;
    }

    @Override
    public List<CultureCoupon> availableCoupons(Long userId, Double orderAmount) {
        // 简化：返回当前在有效期且上架的券，后续可与用户领取表关联
        LambdaQueryWrapper<CultureCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CultureCoupon::getStatus, 1)
               .le(CultureCoupon::getValidStart, LocalDateTime.now())
               .ge(CultureCoupon::getValidEnd, LocalDateTime.now());
        List<CultureCoupon> list = this.list(wrapper);
        if (orderAmount != null) {
            BigDecimal amount = BigDecimal.valueOf(orderAmount);
            list.removeIf(c -> c.getMinSpend() != null && amount.compareTo(c.getMinSpend()) < 0);
        }
        return list;
    }

    @Override
    public Page<?> userCouponPage(Long userId, int page, int size) {
        // 这里为兼容前端返回结构，暂直接复用文化券表，无个人绑定
        return this.page(new Page<>(page, size), new LambdaQueryWrapper<CultureCoupon>()
                .eq(CultureCoupon::getStatus, 1)
                .ge(CultureCoupon::getValidEnd, LocalDateTime.now()));
    }

    @Override
    public Map<String, Object> userCouponStats(Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("available", this.count(new LambdaQueryWrapper<CultureCoupon>()
                .eq(CultureCoupon::getStatus, 1)
                .ge(CultureCoupon::getValidEnd, LocalDateTime.now())));
        map.put("used", 0);
        map.put("expired", this.count(new LambdaQueryWrapper<CultureCoupon>()
                .lt(CultureCoupon::getValidEnd, LocalDateTime.now())));
        return map;
    }
}



















