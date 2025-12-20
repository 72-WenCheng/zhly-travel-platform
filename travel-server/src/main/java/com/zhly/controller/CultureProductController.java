package com.zhly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureProduct;
import com.zhly.service.CultureProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户端-文旅商品")
@RestController
@RequestMapping("/api/culture/products")
@RequiredArgsConstructor
public class CultureProductController {

    private final CultureProductService cultureProductService;

    @Operation(summary = "商品列表")
    @GetMapping("/page")
    public Result<Page<CultureProduct>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type) {
        try {
            Page<CultureProduct> result = cultureProductService.pageList(page, size, keyword, 1, type);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "商品详情")
    @GetMapping("/{id}")
    public Result<CultureProduct> detail(@PathVariable Long id) {
        try {
            CultureProduct product = cultureProductService.getById(id);
            return product == null ? Result.error("商品不存在") : Result.success(product);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }
}
























