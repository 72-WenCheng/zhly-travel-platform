package com.zhly.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.Result;
import com.zhly.entity.CultureAppointment;
import com.zhly.service.CultureAppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理端-文旅预约")
@RestController
@RequestMapping("/api/admin/culture/appointments")
@RequiredArgsConstructor
public class AdminCultureAppointmentController {

    private final CultureAppointmentService cultureAppointmentService;

    @Operation(summary = "分页查询预约")
    @GetMapping("/page")
    public Result<Page<CultureAppointment>> page(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer appointmentType,
            @RequestParam(required = false) Integer status) {
        try {
            Page<CultureAppointment> result = cultureAppointmentService.pageList(page, size, appointmentType, status);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public Result<CultureAppointment> detail(@PathVariable Long id) {
        try {
            CultureAppointment appointment = cultureAppointmentService.getById(id);
            return appointment == null ? Result.error("数据不存在") : Result.success(appointment);
        } catch (Exception e) {
            return Result.error("获取详情失败: " + e.getMessage());
        }
    }

    @Operation(summary = "更新状态")
    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        try {
            boolean ok = cultureAppointmentService.updateStatus(id, body.get("status"));
            return ok ? Result.success("操作成功") : Result.error("操作失败");
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }
}



















