package com.zhly.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zhly.common.R;
import com.zhly.entity.PointsLog;
import com.zhly.entity.UserLevel;
import com.zhly.entity.UserPoints;
import com.zhly.mapper.PointsLogMapper;
import com.zhly.mapper.UserLevelMapper;
import com.zhly.service.IUserPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户端-积分Controller
 * 
 * @author zhly
 * @since 2025-10-24
 */
@RestController
@RequestMapping("/api/user/points")
public class UserPointsController {
    
    @Autowired
    private IUserPointsService userPointsService;
    
    @Autowired
    private UserLevelMapper userLevelMapper;
    
    @Autowired
    private PointsLogMapper pointsLogMapper;
    
    @Autowired(required = false)
    private com.zhly.service.ICouponDistributionService couponDistributionService;
    
    /**
     * 获取我的积分信息
     */
    @GetMapping("/my")
    public R<Map<String, Object>> getMyPoints(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 用户积分信息（会自动同步等级信息）
        UserPoints userPoints = userPointsService.getUserPointsWithLevel(userId);
        result.put("userPoints", userPoints);
        
        // 根据积分获取当前等级（确保等级信息准确）
        int totalPoints = userPoints.getTotalPoints() != null ? userPoints.getTotalPoints() : 0;
        UserLevel currentLevel = userLevelMapper.getLevelByPoints(totalPoints);
        if (currentLevel == null) {
            // 如果没有找到等级，使用默认等级（青铜旅行者，level_code=1）
            currentLevel = userLevelMapper.getLevelByCode(1);
        }
        result.put("currentLevel", currentLevel);
        
        // 下一等级信息
        Integer currentLevelCode = currentLevel != null ? currentLevel.getLevelCode() : 1;
        UserLevel nextLevel = userLevelMapper.getLevelByCode(currentLevelCode + 1);
        result.put("nextLevel", nextLevel);
        
        // 距离下一等级还需积分
        if (nextLevel != null && currentLevel != null) {
            int neededPoints = nextLevel.getRequiredPoints() - totalPoints;
            result.put("neededPoints", Math.max(0, neededPoints));
        } else {
            result.put("neededPoints", 0);
            result.put("isMaxLevel", true);
        }
        
        // 今日行为统计
        int todayPosts = userPointsService.getTodayPostCount(userId);
        int todayComments = userPointsService.getTodayCommentCount(userId);
        if (currentLevel != null) {
            result.put("dailyPostLimit", currentLevel.getDailyPostLimit());
            result.put("dailyCommentLimit", currentLevel.getDailyCommentLimit());
        } else {
            result.put("dailyPostLimit", 0);
            result.put("dailyCommentLimit", 0);
        }
        result.put("todayPosts", todayPosts);
        result.put("todayComments", todayComments);
        
        return R.ok(result);
    }
    
    /**
     * 获取我的积分明细
     */
    @GetMapping("/my-log")
    public R<Page<PointsLog>> getMyLog(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit) {
        
        Page<PointsLog> pageObj = new Page<>(page, limit);
        QueryWrapper<PointsLog> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        pointsLogMapper.selectPage(pageObj, wrapper);
        
        return R.ok(pageObj);
    }
    
    /**
     * 获取所有等级列表（用于展示升级路径）
     */
    @GetMapping("/levels")
    public R<List<UserLevel>> getLevels() {
        QueryWrapper<UserLevel> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("level_code");
        List<UserLevel> list = userLevelMapper.selectList(wrapper);
        return R.ok(list);
    }
    
    /**
     * 检查权限
     */
    @GetMapping("/check-permission")
    public R<Map<String, Boolean>> checkPermission(
            @RequestParam Long userId,
            @RequestParam String permissionType) {
        
        boolean hasPermission = userPointsService.checkPermission(userId, permissionType);
        
        Map<String, Boolean> result = new HashMap<>();
        result.put("hasPermission", hasPermission);
        
        return R.ok(result);
    }
    
    /**
     * 签到获取积分
     */
    @PostMapping("/daily-checkin")
    public R<String> dailyCheckin(@RequestParam Long userId) {
        try {
            // 检查今日是否已签到
            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);
            
            QueryWrapper<PointsLog> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("user_id", userId)
                       .eq("action_type", 1)  // 1-登录/签到
                       .like("action_desc", "签到")
                       .between("create_time", startOfDay, endOfDay);
            
            Long todayCheckinCount = pointsLogMapper.selectCount(checkWrapper);
            if (todayCheckinCount != null && todayCheckinCount > 0) {
                return R.error("今日已签到，明天再来吧！");
            }
            
            // 计算连续签到天数
            int consecutiveDays = calculateConsecutiveCheckinDays(userId);
            int basePoints = 5;  // 基础积分
            int bonusPoints = 0;  // 额外奖励积分
            
            // 连续签到额外奖励：连续3天+2，连续7天+5，连续15天+10，连续30天+20
            if (consecutiveDays >= 30) {
                bonusPoints = 20;
            } else if (consecutiveDays >= 15) {
                bonusPoints = 10;
            } else if (consecutiveDays >= 7) {
                bonusPoints = 5;
            } else if (consecutiveDays >= 3) {
                bonusPoints = 2;
            }
            
            int totalPoints = basePoints + bonusPoints;
            
            // 添加积分
            String actionDesc = bonusPoints > 0 
                ? String.format("每日签到（连续%d天，额外奖励%d积分）", consecutiveDays + 1, bonusPoints)
                : "每日签到";
            userPointsService.addPoints(userId, totalPoints, 1, actionDesc, null, null);
            
            String message = bonusPoints > 0
                ? String.format("签到成功！获得%d积分（基础%d积分 + 连续签到奖励%d积分）", totalPoints, basePoints, bonusPoints)
                : String.format("签到成功，获得%d积分", totalPoints);
            
            return R.ok(message);
        } catch (Exception e) {
            return R.error("签到失败: " + e.getMessage());
        }
    }
    
    /**
     * 计算连续签到天数
     */
    private int calculateConsecutiveCheckinDays(Long userId) {
        int consecutiveDays = 0;
        LocalDate checkDate = LocalDate.now().minusDays(1);  // 从昨天开始检查
        
        while (true) {
            LocalDateTime startOfDay = checkDate.atStartOfDay();
            LocalDateTime endOfDay = checkDate.atTime(LocalTime.MAX);
            
            QueryWrapper<PointsLog> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId)
                   .eq("action_type", 1)  // 1-登录/签到
                   .like("action_desc", "签到")
                   .between("create_time", startOfDay, endOfDay);
            
            Long count = pointsLogMapper.selectCount(wrapper);
            if (count == null || count == 0) {
                break;  // 如果某天没有签到，停止计算
            }
            
            consecutiveDays++;
            checkDate = checkDate.minusDays(1);
            
            // 最多检查30天
            if (consecutiveDays >= 30) {
                break;
            }
        }
        
        return consecutiveDays;
    }
    
    /**
     * 手动触发优惠券发放（用于测试或管理员操作）
     */
    @PostMapping("/distribute-coupons")
    public R<String> distributeCoupons(@RequestParam Long userId) {
        try {
            if (userPointsService == null) {
                return R.error("积分服务未启用");
            }
            
            UserPoints userPoints = userPointsService.getUserPointsWithLevel(userId);
            if (userPoints == null) {
                return R.error("用户积分信息不存在");
            }
            
            UserLevel level = userLevelMapper.getLevelByPoints(userPoints.getTotalPoints());
            if (level == null || level.getLevelCode() == null || level.getLevelCode() < 3) {
                return R.error("您的等级不足，只有黄金游侠及以上等级才能获得优惠券");
            }
            
            if (couponDistributionService == null) {
                return R.error("优惠券发放服务未启用");
            }
            
            int count = couponDistributionService.distributeCouponsByLevel(
                userId, level.getLevelCode(), level.getLevelName());
            
            if (count > 0) {
                return R.ok("成功发放 " + count + " 张优惠券");
            } else {
                return R.ok("本月优惠券已发放完毕");
            }
        } catch (Exception e) {
            return R.error("发放优惠券失败: " + e.getMessage());
        }
    }
}







