package com.zhly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureAttraction;
import com.zhly.service.CultureAttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户端-特色周边")
@RestController
@RequestMapping("/api/culture/attractions")
@RequiredArgsConstructor
public class CultureAttractionController {

    private final CultureAttractionService cultureAttractionService;

    @Operation(summary = "周边列表")
    @GetMapping("/page")
    public Result<Page<CultureAttraction>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer type) {
        try {
            Page<CultureAttraction> result = cultureAttractionService.pageList(page, size, keyword, city, type, 1);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "周边详情")
    @GetMapping("/{id}")
    public Result<CultureAttraction> detail(@PathVariable Long id) {
        try {
            CultureAttraction attraction = cultureAttractionService.getById(id);
            return attraction == null ? Result.error("数据不存在") : Result.success(attraction);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }
}



















