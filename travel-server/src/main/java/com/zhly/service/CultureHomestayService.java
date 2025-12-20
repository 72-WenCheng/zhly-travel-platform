package com.zhly.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zhly.entity.CultureHomestay;

import java.util.Map;

public interface CultureHomestayService extends IService<CultureHomestay> {

    Page<CultureHomestay> pageList(int page, int size, String keyword, String location, Integer status);

    boolean updateStatus(Long id, Integer status);

    Map<String, Object> stats();
}
























