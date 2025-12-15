package com.zhly.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureService;
import com.zhly.entity.CultureServicePackage;
import com.zhly.service.CultureServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Tag(name = "管理端-农家乐服务")
@RestController
@RequestMapping("/api/admin/culture/services")
@RequiredArgsConstructor
public class AdminCultureServiceController {

    private final CultureServiceService cultureServiceService;

    @Operation(summary = "分页查询服务")
    @GetMapping("/page")
    public Result<Page<CultureService>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Integer status) {
        try {
            Page<CultureService> result = cultureServiceService.pageList(page, size, keyword, location, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取服务详情")
    @GetMapping("/{id}")
    public Result<CultureService> detail(@PathVariable Long id) {
        try {
            CultureService detail = cultureServiceService.getDetail(id);
            if (detail == null) {
                return Result.error("服务不存在");
            }
            return Result.success(detail);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新增服务")
    @PostMapping
    public Result<String> create(@RequestBody Map<String, Object> payload) {
        try {
            CultureService service = mapToService(payload);
            List<CultureServicePackage> packages = mapToPackages(payload.get("packages"));
            boolean ok = cultureServiceService.create(service, packages);
            return ok ? Result.success("新增成功") : Result.error("新增失败");
        } catch (Exception e) {
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新服务")
    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            CultureService service = mapToService(payload);
            List<CultureServicePackage> packages = mapToPackages(payload.get("packages"));
            boolean ok = cultureServiceService.update(id, service, packages);
            return ok ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @Operation(summary = "删除服务")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean ok = cultureServiceService.removeWithPackages(id);
            return ok ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @Operation(summary = "切换状态")
    @PutMapping("/{id}/status")
    public Result<String> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> req) {
        try {
            Integer status = req.get("status");
            boolean ok = cultureServiceService.updateStatus(id, status);
            return ok ? Result.success("操作成功") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    @Operation(summary = "统计")
    @GetMapping("/stats")
    public Result<Map<String, Object>> stats() {
        try {
            return Result.success(cultureServiceService.stats());
        } catch (Exception e) {
            return Result.error("获取统计失败: " + e.getMessage());
        }
    }

    private CultureService mapToService(Map<String, Object> payload) {
        CultureService service = new CultureService();
        service.setTitle(string(payload.get("title")));
        service.setLocation(string(payload.get("location")));
        service.setContactPhone(string(payload.get("contactPhone")));
        service.setRating(toDecimal(payload.get("rating")));
        service.setViews(toInt(payload.get("views")));
        service.setDescription(string(payload.get("description")));
        service.setImages(jsonString(payload.get("images")));
        service.setFeatures(jsonString(payload.get("features")));
        service.setFacilities(jsonString(payload.get("facilities")));
        service.setStatus(toInt(payload.get("status")));
        return service;
    }

    @SuppressWarnings("unchecked")
    private List<CultureServicePackage> mapToPackages(Object obj) {
        if (obj == null) return Collections.emptyList();
        try {
            List<Map<String, Object>> list = (List<Map<String, Object>>) obj;
            return list.stream().map(m -> {
                CultureServicePackage pkg = new CultureServicePackage();
                pkg.setId(m.get("id") == null ? null : Long.valueOf(m.get("id").toString()));
                pkg.setName(string(m.get("name")));
                pkg.setPrice(toDecimal(m.get("price")));
                pkg.setUnit(string(m.get("unit")));
                pkg.setDescription(string(m.get("description")));
                pkg.setIncludes(jsonString(m.get("includes")));
                return pkg;
            }).collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String string(Object o) {
        return o == null ? null : o.toString();
    }

    private java.math.BigDecimal toDecimal(Object o) {
        try {
            return o == null ? null : new java.math.BigDecimal(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private Integer toInt(Object o) {
        try {
            return o == null ? null : Integer.valueOf(o.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String jsonString(Object o) {
        if (o == null) return null;
        if (o instanceof String s) return s;
        return com.alibaba.fastjson2.JSON.toJSONString(o);
    }
}

