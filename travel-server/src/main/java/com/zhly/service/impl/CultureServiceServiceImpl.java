package com.zhly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhly.entity.CultureService;
import com.zhly.entity.CultureServicePackage;
import com.zhly.mapper.CultureServiceMapper;
import com.zhly.mapper.CultureServicePackageMapper;
import com.zhly.service.CultureServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CultureServiceServiceImpl extends ServiceImpl<CultureServiceMapper, CultureService> implements CultureServiceService {

    private final CultureServicePackageMapper packageMapper;

    @Override
    public Page<CultureService> pageList(int page, int size, String keyword, String location, Integer status) {
        Page<CultureService> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<CultureService> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(keyword != null && !keyword.isEmpty(), CultureService::getTitle, keyword)
               .like(location != null && !location.isEmpty(), CultureService::getLocation, location)
               .eq(status != null, CultureService::getStatus, status)
               .orderByDesc(CultureService::getUpdateTime);
        return this.page(pageParam, wrapper);
    }

    @Override
    public CultureService getDetail(Long id) {
        CultureService service = this.getById(id);
        if (service != null) {
            List<CultureServicePackage> packages = packageMapper.selectList(
                    new LambdaQueryWrapper<CultureServicePackage>().eq(CultureServicePackage::getServiceId, id));
            service.setPackages(packages);
        }
        return service;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean create(CultureService service, List<CultureServicePackage> packages) {
        boolean saved = this.save(service);
        if (saved && packages != null) {
            for (CultureServicePackage pkg : packages) {
                pkg.setServiceId(service.getId());
                packageMapper.insert(pkg);
            }
        }
        return saved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(Long id, CultureService service, List<CultureServicePackage> packages) {
        service.setId(id);
        boolean updated = this.updateById(service);
        if (updated) {
            // 先删再插
            packageMapper.delete(new LambdaQueryWrapper<CultureServicePackage>().eq(CultureServicePackage::getServiceId, id));
            if (packages != null) {
                for (CultureServicePackage pkg : packages) {
                    pkg.setServiceId(id);
                    packageMapper.insert(pkg);
                }
            }
        }
        return updated;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeWithPackages(Long id) {
        packageMapper.delete(new LambdaQueryWrapper<CultureServicePackage>().eq(CultureServicePackage::getServiceId, id));
        return this.removeById(id);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        CultureService service = this.getById(id);
        if (service == null) return false;
        service.setStatus(status);
        return this.updateById(service);
    }

    @Override
    public Map<String, Object> stats() {
        Map<String, Object> map = new HashMap<>();
        map.put("total", this.count());
        map.put("active", this.count(new LambdaQueryWrapper<CultureService>().eq(CultureService::getStatus, 1)));
        map.put("maintenance", this.count(new LambdaQueryWrapper<CultureService>().eq(CultureService::getStatus, 2)));
        map.put("closed", this.count(new LambdaQueryWrapper<CultureService>().eq(CultureService::getStatus, 0)));
        int totalViews = this.list().stream().mapToInt(item -> item.getViews() == null ? 0 : item.getViews()).sum();
        map.put("totalViews", totalViews);
        return map;
    }
}

