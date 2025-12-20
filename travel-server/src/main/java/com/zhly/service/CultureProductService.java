package com.zhly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhly.entity.CultureProduct;

import java.util.Map;

public interface CultureProductService extends IService<CultureProduct> {

    Page<CultureProduct> pageList(int page, int size, String keyword, Integer status, Integer productType);

    boolean updateStatus(Long id, Integer status);

    Map<String, Object> stats();
}
























