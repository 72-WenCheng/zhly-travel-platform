package com.zhly.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.R;
import com.zhly.entity.CultureProject;
import com.zhly.entity.ProjectApplication;
import com.zhly.mapper.ProjectApplicationMapper;
import com.zhly.service.CultureProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 用户端-项目申请控制器
 */
@Tag(name = "用户端-项目申请")
@RestController
@RequestMapping("/api/user/culture/application")
@RequiredArgsConstructor
public class UserProjectApplicationController {
    
    private final ProjectApplicationMapper projectApplicationMapper;
    private final CultureProjectService cultureProjectService;
    
    @Operation(summary = "获取我的申请列表")
    @GetMapping("/my")
    public R<Page<ProjectApplication>> getMyApplications(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        try {
            // 从JWT token中获取当前登录用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.error("请先登录");
            }
            
            Page<ProjectApplication> pageParam = new Page<>(page, size);
            
            // 查询当前用户的申请列表
            Page<ProjectApplication> result = projectApplicationMapper.selectPage(
                pageParam,
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProjectApplication>()
                    .eq(ProjectApplication::getUserId, userId)
                    .eq(status != null, ProjectApplication::getStatus, status)
                    .orderByDesc(ProjectApplication::getSubmitTime)
            );
            
            return R.ok(result);
        } catch (Exception e) {
            return R.error("获取申请列表失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "获取申请详情")
    @GetMapping("/{id}")
    public R<ProjectApplication> getApplication(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 从JWT token中获取当前登录用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.error("请先登录");
            }
            
            ProjectApplication application = projectApplicationMapper.selectById(id);
            if (application == null) {
                return R.error("申请不存在");
            }
            
            // 验证是否为当前用户的申请
            if (!application.getUserId().equals(userId)) {
                return R.error("无权访问此申请");
            }
            
            return R.ok(application);
        } catch (Exception e) {
            return R.error("获取申请详情失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "提交项目申请")
    @PostMapping("/submit")
    public R<ProjectApplication> submitApplication(@RequestBody Map<String, Object> applicationData, HttpServletRequest request) {
        try {
            // 从JWT token中获取当前登录用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.error("请先登录");
            }
            
            // 获取项目信息，用于填充region等字段
            Long projectId = Long.valueOf(applicationData.get("projectId").toString());
            CultureProject project = cultureProjectService.getProjectById(projectId);
            if (project == null) {
                return R.error("项目不存在");
            }
            
            ProjectApplication application = new ProjectApplication();
            application.setUserId(userId);
            application.setProjectId(projectId);
            application.setProjectTitle(applicationData.get("projectTitle") != null ? applicationData.get("projectTitle").toString() : project.getName());
            
            // 申请人信息
            application.setApplicantName(applicationData.get("organizationName") != null ? applicationData.get("organizationName").toString() : "");
            application.setContactName(applicationData.get("applicantName") != null ? applicationData.get("applicantName").toString() : "");
            application.setContactPhone(applicationData.get("phone") != null ? applicationData.get("phone").toString() : "");
            application.setContactEmail(applicationData.get("email") != null ? applicationData.get("email").toString() : "");
            // 职务
            application.setPosition(applicationData.get("position") != null ? applicationData.get("position").toString() : "");
            // 合作意向
            application.setCooperationType(applicationData.get("cooperationType") != null ? applicationData.get("cooperationType").toString() : "");
            // 详细地址必填列：前端没传时写空串以避免数据库默认值错误
            Object detailedAddress = applicationData.get("detailedAddress") != null ? applicationData.get("detailedAddress") : applicationData.get("address");
            application.setDetailedAddress(detailedAddress != null ? detailedAddress.toString() : "");
            
            // 地区信息：从项目信息中获取
            application.setRegion(project.getRegion() != null ? project.getRegion() : "");
            
            // 申请金额
            if (applicationData.get("investmentAmount") != null) {
                try {
                    application.setApplyAmount(new BigDecimal(applicationData.get("investmentAmount").toString()));
                } catch (Exception e) {
                    application.setApplyAmount(BigDecimal.ZERO);
                }
            } else {
                application.setApplyAmount(BigDecimal.ZERO);
            }
            
            // 申请说明
            application.setProjectDescription(applicationData.get("description") != null ? applicationData.get("description").toString() : "");
            
            // 申请人类型：默认为企业(2)
            application.setApplicantType(2);
            
            // 状态：待审核
            application.setStatus(1);
            
            // 生成申请编号：AP + 年月日 + 4位随机数
            String applicationNo = "AP" + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) 
                + String.format("%04d", (int)(Math.random() * 10000));
            application.setApplicationNo(applicationNo);
            
            // 提交时间
            application.setSubmitTime(java.time.LocalDateTime.now());
            
            // 保存申请
            projectApplicationMapper.insert(application);
            
            return R.ok("申请提交成功", application);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("提交申请失败: " + e.getMessage());
        }
    }
    
    @Operation(summary = "撤回申请")
    @PutMapping("/{id}/withdraw")
    public R<String> withdrawApplication(@PathVariable Long id, HttpServletRequest request) {
        try {
            // 从JWT token中获取当前登录用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return R.error("请先登录");
            }
            
            ProjectApplication application = projectApplicationMapper.selectById(id);
            if (application == null) {
                return R.error("申请不存在");
            }
            
            // 验证是否为当前用户的申请
            if (!application.getUserId().equals(userId)) {
                return R.error("无权操作此申请");
            }
            
            // 只有待审核或审核中状态的申请才能撤回
            if (application.getStatus() != 1 && application.getStatus() != 2) {
                return R.error("只有待审核或审核中的申请才能撤回");
            }
            
            application.setStatus(5); // 5-已撤回
            projectApplicationMapper.updateById(application);
            
            return R.ok("申请已撤回");
        } catch (Exception e) {
            return R.error("撤回申请失败: " + e.getMessage());
        }
    }
}

