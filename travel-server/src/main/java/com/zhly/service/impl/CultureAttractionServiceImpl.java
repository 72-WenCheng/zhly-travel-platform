package com.zhly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhly.entity.CultureAttraction;
import com.zhly.mapper.CultureAttractionMapper;
import com.zhly.service.CultureAttractionService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CultureAttractionServiceImpl extends ServiceImpl<CultureAttractionMapper, CultureAttraction> implements CultureAttractionService {

    @Override
    public Page<CultureAttraction> pageList(int page, int size, String keyword, String city, Integer type, Integer status) {
        Page<CultureAttraction> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CultureAttraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.isEmpty(), CultureAttraction::getName, keyword)
               .like(city != null && !city.isEmpty(), CultureAttraction::getCity, city)
               .eq(type != null, CultureAttraction::getType, type)
               .eq(status != null, CultureAttraction::getStatus, status)
               .orderByDesc(CultureAttraction::getUpdateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        CultureAttraction attraction = this.getById(id);
        if (attraction == null) return false;
        attraction.setStatus(status);
        return this.updateById(attraction);
    }

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("total", this.count());
        map.put("active", this.count(new LambdaQueryWrapper<CultureAttraction>().eq(CultureAttraction::getStatus, 1)));
        map.put("closed", this.count(new LambdaQueryWrapper<CultureAttraction>().eq(CultureAttraction::getStatus, 0)));
        int totalViews = this.list().stream().mapToInt(item -> item.getViewCount() == null ? 0 : item.getViewCount()).sum();
        map.put("totalViews", totalViews);
        return map;
    }
}
























