package com.zhly.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureHomestay;
import com.zhly.service.CultureHomestayService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理端-特色民宿")
@RestController
@RequestMapping("/api/admin/culture/homestays")
@RequiredArgsConstructor
public class AdminCultureHomestayController {

    private final CultureHomestayService cultureHomestayService;

    @Operation(summary = "分页查询民宿")
    @GetMapping("/page")
    public Result<Page<CultureHomestay>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer status) {
        try {
            Page<CultureHomestay> result = cultureHomestayService.pageList(page, size, keyword, location, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取民宿详情")
    @GetMapping("/{id}")
    public Result<CultureHomestay> detail(@PathVariable Long id) {
        try {
            CultureHomestay homestay = cultureHomestayService.getById(id);
            return homestay == null ? Result.error("民宿不存在") : Result.success(homestay);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增民宿")
    @PostMapping
    public Result<String> create(@RequestBody CultureHomestay homestay) {
        try {
            boolean ok = cultureHomestayService.save(homestay);
            return ok ? Result.success("新增成功") : Result.error("新增失败");
        } catch (Exception e) {
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新民宿")
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody CultureHomestay homestay) {
        try {
            homestay.setId(id);
            boolean ok = cultureHomestayService.updateById(homestay);
            return ok ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除民宿")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean ok = cultureHomestayService.removeById(id);
            return ok ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "切换状态")
    @PutMapping("/{id}/status")
    public Result<String> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            boolean ok = cultureHomestayService.updateStatus(id, body.get("status"));
            return ok ? Result.success("操作成功") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        try {
            return Result.success(cultureHomestayService.stats());
        } catch (Exception e) {
            return Result.error("获取统计失败: " + e.getMessage());
        }
    }
}



















