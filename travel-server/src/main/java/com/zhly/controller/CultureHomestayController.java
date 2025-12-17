package com.zhly.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureHomestay;
import com.zhly.service.CultureHomestayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户端-特色民宿")
@RestController
@RequestMapping("/api/culture/homestays")
@RequiredArgsConstructor
public class CultureHomestayController {

    private final CultureHomestayService cultureHomestayService;

    @Operation(summary = "民宿列表")
    @GetMapping("/page")
    public Result<Page<CultureHomestay>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location) {
        try {
            Page<CultureHomestay> result = cultureHomestayService.pageList(page, size, keyword, location, 1);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取列表失败: " + e.getMessage());
        }
    }

    @Operation(summary = "民宿详情")
    @GetMapping("/{id}")
    public Result<CultureHomestay> detail(@PathVariable Long id) {
        try {
            CultureHomestay homestay = cultureHomestayService.getById(id);
            return homestay == null ? Result.error("民宿不存在") : Result.success(homestay);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }
}



















