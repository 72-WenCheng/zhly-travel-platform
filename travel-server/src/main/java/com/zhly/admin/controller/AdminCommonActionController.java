package com.zhly.admin.controller;

import com.zhly.admin.entity.AdminCommonActionUsage;
import com.zhly.admin.service.AdminCommonActionUsageService;
import com.zhly.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员常用操作控制器
 */
@Tag(name = "管理端-常用操作统计")
@RestController
@RequestMapping("/api/admin/usage/common-actions")
@RequiredArgsConstructor
public class AdminCommonActionController {

    private final AdminCommonActionUsageService actionUsageService;

    @Operation(summary = "记录常用操作点击")
    @PostMapping
    public Result<String> recordUsage(@RequestBody Map<String, String> payload,
                                      HttpServletRequest request) {
        try {
            Long adminId = (Long) request.getAttribute("userId");
            if (adminId == null) {
                // 静默失败，不返回错误，避免影响用户体验
                return Result.success("记录成功", null);
            }

            String actionCode = payload.get("actionCode");
            String actionName = payload.get("actionName");
            String actionPath = payload.get("actionPath");

            if (!StringUtils.hasText(actionCode) || !StringUtils.hasText(actionPath)) {
                // 参数不完整时也静默失败
                return Result.success("记录成功", null);
            }

            AdminCommonActionUsage usage = new AdminCommonActionUsage();
            usage.setActionCode(actionCode);
            usage.setActionName(StringUtils.hasText(actionName) ? actionName : actionCode);
            usage.setActionPath(actionPath);
            usage.setActionIcon(payload.getOrDefault("actionIcon", "Operation"));
            usage.setActionDesc(payload.get("actionDesc"));

            actionUsageService.recordUsage(adminId, usage);
            return Result.success("记录成功", null);
        } catch (Exception e) {
            // 任何异常都静默处理，避免影响用户体验
            return Result.success("记录成功", null);
        }
    }

    @Operation(summary = "获取管理员常用操作列表")
    @GetMapping
    public Result<List<AdminCommonActionUsage>> listCommonActions(
            @RequestParam(value = "limit", required = false, defaultValue = "3") Integer limit,
            HttpServletRequest request) {
        try {
            Long adminId = (Long) request.getAttribute("userId");
            if (adminId == null) {
                // 获取不到用户ID时返回空列表，不返回错误
                return Result.success("获取成功", java.util.Collections.emptyList());
            }

            List<AdminCommonActionUsage> actions = actionUsageService.listCommonActions(adminId, limit);
            return Result.success("获取成功", actions);
        } catch (Exception e) {
            // 任何异常都返回空列表，避免影响用户体验
            return Result.success("获取成功", java.util.Collections.emptyList());
        }
    }
}

