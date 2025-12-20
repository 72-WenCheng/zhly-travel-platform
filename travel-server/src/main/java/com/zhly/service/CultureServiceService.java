package com.zhly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhly.entity.CultureService;
import com.zhly.entity.CultureServicePackage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Map;

public interface CultureServiceService extends IService<CultureService> {

    Page<CultureService> pageList(int page, int size, String keyword, String location, Integer status);

    CultureService getDetail(Long id);

    boolean create(CultureService service, List<CultureServicePackage> packages);

    boolean update(Long id, CultureService service, List<CultureServicePackage> packages);

    boolean removeWithPackages(Long id);

    boolean updateStatus(Long id, Integer status);

    Map<String, Object> stats();
}
























