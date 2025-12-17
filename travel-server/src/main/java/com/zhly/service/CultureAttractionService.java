package com.zhly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhly.entity.CultureAttraction;

import java.util.Map;

public interface CultureAttractionService extends IService<CultureAttraction> {

    Page<CultureAttraction> pageList(int page, int size, String keyword, String city, Integer type, Integer status);

    boolean updateStatus(Long id, Integer status);

    Map<String, Object> stats();
}



















