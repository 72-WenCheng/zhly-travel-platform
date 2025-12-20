package com.zhly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhly.entity.CultureCoupon;

import java.util.List;
import java.util.Map;

public interface CultureCouponService extends IService<CultureCoupon> {

    Page<CultureCoupon> pageList(int page, int size, String name, Integer type, Integer status);

    boolean updateStatus(Long id, Integer status);

    Map<String, Object> stats();

    List<CultureCoupon> availableCoupons(Long userId, Double orderAmount);

    Page<?> userCouponPage(Long userId, int page, int size);

    Map<String, Object> userCouponStats(Long userId);
}
























