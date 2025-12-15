package com.zhly.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureAttraction;
import com.zhly.service.CultureAttractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理端-特色周边")
@RestController
@RequestMapping("/api/admin/culture/attractions")
@RequiredArgsConstructor
public class AdminCultureAttractionController {

    private final CultureAttractionService cultureAttractionService;

    @Operation(summary = "分页查询周边")
    @GetMapping("/page")
    public Result<Page<CultureAttraction>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        try {
            Page<CultureAttraction> result = cultureAttractionService.pageList(page, size, keyword, city, type, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public Result<CultureAttraction> detail(@PathVariable Long id) {
        try {
            CultureAttraction attraction = cultureAttractionService.getById(id);
            return attraction == null ? Result.error("数据不存在") : Result.success(attraction);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<String> create(@RequestBody CultureAttraction attraction) {
        try {
            boolean ok = cultureAttractionService.save(attraction);
            return ok ? Result.success("新增成功") : Result.error("新增失败");
        } catch (Exception e) {
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新")
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody CultureAttraction attraction) {
        try {
            attraction.setId(id);
            boolean ok = cultureAttractionService.updateById(attraction);
            return ok ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean ok = cultureAttractionService.removeById(id);
            return ok ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "切换状态")
    @PutMapping("/{id}/status")
    public Result<String> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            boolean ok = cultureAttractionService.updateStatus(id, body.get("status"));
            return ok ? Result.success("操作成功") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        try {
            return Result.success(cultureAttractionService.stats());
        } catch (Exception e) {
            return Result.error("获取统计失败: " + e.getMessage());
        }
    }
}





