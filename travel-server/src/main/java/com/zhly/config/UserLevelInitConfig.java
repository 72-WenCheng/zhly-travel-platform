package com.zhly.config;

import com.zhly.entity.UserLevel;
import com.zhly.mapper.UserLevelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 用户等级初始化配置
 * 应用启动时自动初始化用户等级数据
 * 
 * @author zhly
 * @since 2025-01-04
 */
@Component
@Order(1) // 优先执行
public class UserLevelInitConfig implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(UserLevelInitConfig.class);
    
    @Autowired
    private UserLevelMapper userLevelMapper;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化用户等级数据...");
        initUserLevels();
        logger.info("用户等级数据初始化完成");
    }
    
    /**
     * 初始化用户等级数据
     */
    private void initUserLevels() {
        // 检查是否已有数据
        long count = userLevelMapper.selectCount(null);
        if (count > 0) {
            logger.info("用户等级数据已存在，跳过初始化");
            // 确保权限配置正确
            ensurePermissionConfig();
            return;
        }
        
        logger.info("开始创建用户等级数据...");
        
        // 1. 青铜旅行者
        createLevel(1, "青铜旅行者", 0, "#8B7355", "User", 0, 1, 0, 0, 10);
        
        // 2. 白银探索者
        createLevel(2, "白银探索者", 100, "#9CA3AF", "Aim", 1, 1, 0, 2, 15);
        
        // 3. 黄金游侠
        createLevel(3, "黄金游侠", 500, "#F59E0B", "Medal", 1, 1, 0, 5, 30);
        
        // 4. 铂金旅者
        createLevel(4, "铂金旅者", 2000, "#6366F1", "Trophy", 1, 1, 1, 10, 50);
        
        // 5. 钻石达人
        createLevel(5, "钻石达人", 5000, "#EC4899", "StarFilled", 1, 1, 1, 20, 100);
        
        // 6. 王者导师
        createLevel(6, "王者导师", 10000, "#F97316", "TrophyBase", 1, 1, 1, 999, 999);
        
        logger.info("用户等级数据创建完成，共创建 {} 个等级", 6);
    }
    
    /**
     * 创建等级
     */
    private void createLevel(int levelCode, String levelName, int requiredPoints, 
                           String levelColor, String levelIcon,
                           int canPostPlan, int canComment, int canViewPremium,
                           int dailyPostLimit, int dailyCommentLimit) {
        // 检查是否已存在
        UserLevel existing = userLevelMapper.getLevelByCode(levelCode);
        if (existing != null) {
            logger.debug("等级 {} 已存在，跳过创建", levelName);
            // 确保权限配置正确
            updateLevelIfNeeded(existing, canPostPlan, canComment, canViewPremium, 
                              dailyPostLimit, dailyCommentLimit);
            return;
        }
        
        UserLevel level = new UserLevel();
        level.setLevelCode(levelCode);
        level.setLevelNumber(levelCode); // level_number 与 level_code 相同
        level.setLevelName(levelName);
        level.setRequiredPoints(requiredPoints);
        level.setLevelColor(levelColor);
        level.setLevelIcon(levelIcon);
        level.setCanPostPlan(canPostPlan);
        level.setCanComment(canComment);
        level.setCanViewPremium(canViewPremium);
        level.setDailyPostLimit(dailyPostLimit);
        level.setDailyCommentLimit(dailyCommentLimit);
        
        userLevelMapper.insert(level);
        logger.debug("创建等级: {}", levelName);
    }
    
    /**
     * 确保权限配置正确（修复已有数据）
     */
    private void ensurePermissionConfig() {
        logger.info("检查并修复用户等级权限配置...");
        
        // 修复白银探索者及以上等级的发布权限
        updateLevelPermission(2, "白银探索者", 1, 1, 0, 2, 15);
        updateLevelPermission(3, "黄金游侠", 1, 1, 0, 5, 30);
        updateLevelPermission(4, "铂金旅者", 1, 1, 1, 10, 50);
        updateLevelPermission(5, "钻石达人", 1, 1, 1, 20, 100);
        updateLevelPermission(6, "王者导师", 1, 1, 1, 999, 999);
        
        // 确保青铜旅行者不能发布攻略
        updateLevelPermission(1, "青铜旅行者", 0, 1, 0, 0, 10);
        
        logger.info("用户等级权限配置检查完成");
    }
    
    /**
     * 更新等级权限（如果需要）
     */
    private void updateLevelPermission(int levelCode, String levelName, 
                                     int canPostPlan, int canComment, int canViewPremium,
                                     int dailyPostLimit, int dailyCommentLimit) {
        UserLevel level = userLevelMapper.getLevelByCode(levelCode);
        if (level == null) {
            logger.warn("等级 {} (code: {}) 不存在，跳过更新", levelName, levelCode);
            return;
        }
        
        boolean needUpdate = false;
        
        if (level.getCanPostPlan() == null || !level.getCanPostPlan().equals(canPostPlan)) {
            level.setCanPostPlan(canPostPlan);
            needUpdate = true;
        }
        
        if (level.getCanComment() == null || !level.getCanComment().equals(canComment)) {
            level.setCanComment(canComment);
            needUpdate = true;
        }
        
        if (level.getCanViewPremium() == null || !level.getCanViewPremium().equals(canViewPremium)) {
            level.setCanViewPremium(canViewPremium);
            needUpdate = true;
        }
        
        if (level.getDailyPostLimit() == null || !level.getDailyPostLimit().equals(dailyPostLimit)) {
            level.setDailyPostLimit(dailyPostLimit);
            needUpdate = true;
        }
        
        if (level.getDailyCommentLimit() == null || !level.getDailyCommentLimit().equals(dailyCommentLimit)) {
            level.setDailyCommentLimit(dailyCommentLimit);
            needUpdate = true;
        }
        
        if (needUpdate) {
            userLevelMapper.updateById(level);
            logger.info("更新等级权限: {} (code: {})", levelName, levelCode);
        }
    }
    
    /**
     * 更新等级（如果需要）
     */
    private void updateLevelIfNeeded(UserLevel existing, int canPostPlan, int canComment, 
                                   int canViewPremium, int dailyPostLimit, int dailyCommentLimit) {
        boolean needUpdate = false;
        
        if (existing.getCanPostPlan() == null || !existing.getCanPostPlan().equals(canPostPlan)) {
            existing.setCanPostPlan(canPostPlan);
            needUpdate = true;
        }
        
        if (existing.getCanComment() == null || !existing.getCanComment().equals(canComment)) {
            existing.setCanComment(canComment);
            needUpdate = true;
        }
        
        if (existing.getCanViewPremium() == null || !existing.getCanViewPremium().equals(canViewPremium)) {
            existing.setCanViewPremium(canViewPremium);
            needUpdate = true;
        }
        
        if (existing.getDailyPostLimit() == null || !existing.getDailyPostLimit().equals(dailyPostLimit)) {
            existing.setDailyPostLimit(dailyPostLimit);
            needUpdate = true;
        }
        
        if (existing.getDailyCommentLimit() == null || !existing.getDailyCommentLimit().equals(dailyCommentLimit)) {
            existing.setDailyCommentLimit(dailyCommentLimit);
            needUpdate = true;
        }
        
        if (needUpdate) {
            userLevelMapper.updateById(existing);
            logger.debug("更新等级: {}", existing.getLevelName());
        }
    }
}

