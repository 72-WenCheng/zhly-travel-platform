package com.zhly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhly.entity.CultureHomestay;
import com.zhly.mapper.CultureHomestayMapper;
import com.zhly.service.CultureHomestayService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CultureHomestayServiceImpl extends ServiceImpl<CultureHomestayMapper, CultureHomestay> implements CultureHomestayService {

    @Override
    public Page<CultureHomestay> pageList(int page, int size, String keyword, String location, Integer status) {
        Page<CultureHomestay> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CultureHomestay> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.isEmpty(), CultureHomestay::getTitle, keyword)
               .like(location != null && !location.isEmpty(), CultureHomestay::getLocation, location)
               .eq(status != null, CultureHomestay::getStatus, status)
               .orderByDesc(CultureHomestay::getUpdateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        CultureHomestay homestay = this.getById(id);
        if (homestay == null) return false;
        homestay.setStatus(status);
        return this.updateById(homestay);
    }

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("total", this.count());
        map.put("active", this.count(new LambdaQueryWrapper<CultureHomestay>().eq(CultureHomestay::getStatus, 1)));
        map.put("maintenance", this.count(new LambdaQueryWrapper<CultureHomestay>().eq(CultureHomestay::getStatus, 2)));
        map.put("closed", this.count(new LambdaQueryWrapper<CultureHomestay>().eq(CultureHomestay::getStatus, 0)));
        int totalViews = this.list().stream().mapToInt(item -> item.getViews() == null ? 0 : item.getViews()).sum();
        map.put("totalViews", totalViews);
        return map;
    }
}





