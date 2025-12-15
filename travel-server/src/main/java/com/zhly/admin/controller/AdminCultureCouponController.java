package com.zhly.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureCoupon;
import com.zhly.service.CultureCouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理端-文旅优惠券")
@RestController
@RequestMapping("/api/admin/culture/coupon/v2")
@RequiredArgsConstructor
public class AdminCultureCouponController {

    private final CultureCouponService cultureCouponService;

    @Operation(summary = "分页查询优惠券")
    @GetMapping("/page")
    public Result<Page<CultureCoupon>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        try {
            Page<CultureCoupon> result = cultureCouponService.pageList(page, size, name, type, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增优惠券")
    @PostMapping
    public Result<String> create(@RequestBody CultureCoupon coupon) {
        try {
            boolean ok = cultureCouponService.save(coupon);
            return ok ? Result.success("新增成功") : Result.error("新增失败");
        } catch (Exception e) {
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新优惠券")
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody CultureCoupon coupon) {
        try {
            coupon.setId(id);
            boolean ok = cultureCouponService.updateById(coupon);
            return ok ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean ok = cultureCouponService.removeById(id);
            return ok ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "切换状态")
    @PutMapping("/{id}/status")
    public Result<String> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            boolean ok = cultureCouponService.updateStatus(id, body.get("status"));
            return ok ? Result.success("操作成功") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        try {
            return Result.success(cultureCouponService.stats());
        } catch (Exception e) {
            return Result.error("获取统计失败: " + e.getMessage());
        }
    }
}


