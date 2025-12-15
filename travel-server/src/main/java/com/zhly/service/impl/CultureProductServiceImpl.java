package com.zhly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhly.entity.CultureProduct;
import com.zhly.mapper.CultureProductMapper;
import com.zhly.service.CultureProductService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CultureProductServiceImpl extends ServiceImpl<CultureProductMapper, CultureProduct> implements CultureProductService {

    @Override
    public Page<CultureProduct> pageList(int page, int size, String keyword, Integer status, Integer productType) {
        Page<CultureProduct> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CultureProduct> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.isEmpty(), CultureProduct::getProductName, keyword)
               .eq(status != null, CultureProduct::getStatus, status)
               .eq(productType != null, CultureProduct::getProductType, productType)
               .orderByDesc(CultureProduct::getUpdateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        CultureProduct product = this.getById(id);
        if (product == null) return false;
        product.setStatus(status);
        return this.updateById(product);
    }

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("total", this.count());
        map.put("active", this.count(new LambdaQueryWrapper<CultureProduct>().eq(CultureProduct::getStatus, 1)));
        map.put("closed", this.count(new LambdaQueryWrapper<CultureProduct>().eq(CultureProduct::getStatus, 0)));
        map.put("maintenance", this.count(new LambdaQueryWrapper<CultureProduct>().eq(CultureProduct::getStatus, 2)));
        int totalSales = this.list().stream().mapToInt(p -> p.getSalesCount() == null ? 0 : p.getSalesCount()).sum();
        map.put("totalSales", totalSales);
        return map;
    }
}





