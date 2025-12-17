package com.zhly.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureProduct;
import com.zhly.service.CultureProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理端-文旅商品/农特产品")
@RestController
@RequestMapping("/api/admin/culture/products")
@RequiredArgsConstructor
public class AdminCultureProductController {

    private final CultureProductService cultureProductService;

    @Operation(summary = "分页查询产品")
    @GetMapping("/page")
    public Result<Page<CultureProduct>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type) {
        try {
            Page<CultureProduct> result = cultureProductService.pageList(page, size, keyword, status, type);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public Result<CultureProduct> detail(@PathVariable Long id) {
        try {
            CultureProduct product = cultureProductService.getById(id);
            return product == null ? Result.error("产品不存在") : Result.success(product);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增")
    @PostMapping
    public Result<String> create(@RequestBody CultureProduct product) {
        try {
            boolean ok = cultureProductService.save(product);
            return ok ? Result.success("新增成功") : Result.error("新增失败");
        } catch (Exception e) {
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新")
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody CultureProduct product) {
        try {
            product.setId(id);
            boolean ok = cultureProductService.updateById(product);
            return ok ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean ok = cultureProductService.removeById(id);
            return ok ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "切换状态")
    @PutMapping("/{id}/status")
    public Result<String> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            boolean ok = cultureProductService.updateStatus(id, body.get("status"));
            return ok ? Result.success("操作成功") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        try {
            return Result.success(cultureProductService.stats());
        } catch (Exception e) {
            return Result.error("获取统计失败: " + e.getMessage());
        }
    }
}



















