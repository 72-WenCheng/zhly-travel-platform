package com.zhly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureService;
import com.zhly.service.CultureServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户端-农家乐服务")
@RestController
@RequestMapping("/api/culture/services")
@RequiredArgsConstructor
public class CultureServiceController {

    private final CultureServiceService cultureServiceService;

    @Operation(summary = "服务列表")
    @GetMapping("/page")
    public Result<Page<CultureService>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location) {
        try {
            Page<CultureService> result = cultureServiceService.pageList(page, size, keyword, location, 1);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "服务详情")
    @GetMapping("/{id}")
    public Result<CultureService> detail(@PathVariable Long id) {
        try {
            CultureService detail = cultureServiceService.getDetail(id);
            return detail == null ? Result.error("服务不存在") : Result.success(detail);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }
}



















