package com.zhly.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhly.entity.Coupon;
import com.zhly.entity.UserCoupon;
import com.zhly.entity.UserLevel;
import com.zhly.entity.UserPoints;
import com.zhly.mapper.CouponMapper;
import com.zhly.mapper.UserCouponMapper;
import com.zhly.mapper.UserLevelMapper;
import com.zhly.mapper.UserPointsMapper;
import com.zhly.service.ICouponDistributionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * 优惠券发放服务实现类
 * 
 * @author zhly
 * @since 2025-01-01
 */
@Service
public class CouponDistributionServiceImpl implements ICouponDistributionService {
    
    @Autowired
    private UserCouponMapper userCouponMapper;
    
    @Autowired
    private CouponMapper couponMapper;
    
    @Autowired
    private UserLevelMapper userLevelMapper;
    
    @Autowired
    private UserPointsMapper userPointsMapper;
    
    /**
     * 等级对应的优惠券配置
     * levelCode -> {数量, 满减金额, 最低使用金额}
     */
    private static final int[][] LEVEL_COUPON_CONFIG = {
        {}, // 0: 无
        {}, // 1: 青铜旅行者 - 无优惠券
        {}, // 2: 白银探索者 - 无优惠券
        {1, 10, 50},  // 3: 黄金游侠 - 1张满50减10
        {2, 20, 100}, // 4: 铂金旅者 - 2张满100减20
        {3, 50, 200}, // 5: 钻石达人 - 3张满200减50
        {5, 100, 300} // 6: 王者导师 - 5张满300减100
    };
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int distributeCouponsByLevel(Long userId, Integer levelCode, String levelName) {
        if (userId == null || levelCode == null || levelCode < 3) {
            return 0; // 只有黄金游侠及以上等级才有优惠券
        }
        
        if (levelCode > 6) {
            levelCode = 6; // 最高等级
        }
        
        int[] config = LEVEL_COUPON_CONFIG[levelCode];
        if (config == null || config.length == 0) {
            return 0;
        }
        
        int count = config[0];
        BigDecimal discountValue = new BigDecimal(config[1]);
        BigDecimal minAmount = new BigDecimal(config[2]);
        
        // 检查本月是否已发放（避免重复发放）
        YearMonth currentMonth = YearMonth.now();
        Long existingCount = userCouponMapper.countByUserAndMonth(
            userId, 1, currentMonth.getYear(), currentMonth.getMonthValue());
        
        if (existingCount != null && existingCount >= count) {
            System.out.println("ℹ️ 用户 " + userId + " 本月已发放过优惠券，跳过");
            return 0;
        }
        
        // 计算需要发放的数量（如果已发放部分，只发放剩余部分）
        int needDistribute = count - (existingCount != null ? existingCount.intValue() : 0);
        if (needDistribute <= 0) {
            return 0;
        }
        
        int distributedCount = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime validEndTime = now.plusMonths(1).withDayOfMonth(1).minusDays(1)
            .withHour(23).withMinute(59).withSecond(59); // 本月最后一天
        
        for (int i = 0; i < needDistribute; i++) {
            // 创建或查找对应的优惠券模板
            Coupon coupon = findOrCreateCouponTemplate(levelCode, discountValue, minAmount);
            if (coupon == null) {
                System.err.println("⚠️ 创建优惠券模板失败，跳过发放");
                continue;
            }
            
            // 创建用户优惠券记录
            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(coupon.getId());
            userCoupon.setCouponName(coupon.getName());
            userCoupon.setCouponType(coupon.getType());
            userCoupon.setDiscountValue(coupon.getDiscountValue());
            userCoupon.setMinAmount(coupon.getMinAmount());
            userCoupon.setSourceType(1); // 1-等级权益自动发放
            userCoupon.setSourceDesc(levelName + "等级权益");
            userCoupon.setStatus(1); // 1-未使用
            userCoupon.setValidStartTime(now);
            userCoupon.setValidEndTime(validEndTime);
            userCoupon.setCreateTime(now);
            userCoupon.setUpdateTime(now);
            
            userCouponMapper.insert(userCoupon);
            distributedCount++;
            
            System.out.println("✅ 用户 " + userId + " (" + levelName + ") 获得优惠券：" + 
                             coupon.getName() + "，有效期至：" + validEndTime);
        }
        
        return distributedCount;
    }
    
    /**
     * 查找或创建优惠券模板
     */
    private Coupon findOrCreateCouponTemplate(Integer levelCode, BigDecimal discountValue, BigDecimal minAmount) {
        // 根据等级和金额查找是否已有模板
        String couponName = generateCouponName(levelCode, discountValue, minAmount);
        
        QueryWrapper<Coupon> wrapper = new QueryWrapper<>();
        wrapper.eq("name", couponName)
               .eq("type", 1) // 1-满减券
               .eq("discount_type", 1) // 1-固定金额
               .eq("discount_value", discountValue)
               .eq("min_amount", minAmount)
               .eq("status", 2); // 2-进行中
        
        Coupon coupon = couponMapper.selectOne(wrapper);
        
        if (coupon == null) {
            // 创建新的优惠券模板
            coupon = new Coupon();
            coupon.setName(couponName);
            coupon.setType(1); // 1-满减券
            coupon.setDiscountType(1); // 1-固定金额
            coupon.setDiscountValue(discountValue);
            coupon.setMinAmount(minAmount);
            coupon.setTotalCount(999999); // 无限量
            coupon.setRemainingCount(999999);
            coupon.setPerUserLimit(999); // 每人限领999张
            coupon.setScope(1); // 1-全场通用
            coupon.setValidDays(30); // 有效期30天
            coupon.setStatus(2); // 2-进行中
            coupon.setDescription("等级权益优惠券");
            coupon.setCreateTime(LocalDateTime.now());
            coupon.setUpdateTime(LocalDateTime.now());
            
            couponMapper.insert(coupon);
            System.out.println("✅ 创建优惠券模板：" + couponName);
        }
        
        return coupon;
    }
    
    /**
     * 生成优惠券名称
     */
    private String generateCouponName(Integer levelCode, BigDecimal discountValue, BigDecimal minAmount) {
        String levelName = "";
        switch (levelCode) {
            case 3: levelName = "黄金游侠"; break;
            case 4: levelName = "铂金旅者"; break;
            case 5: levelName = "钻石达人"; break;
            case 6: levelName = "王者导师"; break;
        }
        return levelName + "专属券：满" + minAmount.intValue() + "减" + discountValue.intValue();
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void distributeMonthlyCoupons() {
        System.out.println("🔄 开始执行每月优惠券自动发放任务...");
        
        try {
            // 查询所有有积分的用户
            List<UserPoints> allUsers = userPointsMapper.selectList(new QueryWrapper<>());
            
            int totalDistributed = 0;
            int totalUsers = 0;
            
            for (UserPoints userPoints : allUsers) {
                if (userPoints.getUserId() == null || userPoints.getTotalPoints() == null) {
                    continue;
                }
                
                // 根据积分获取等级
                UserLevel level = userLevelMapper.getLevelByPoints(userPoints.getTotalPoints());
                if (level == null || level.getLevelCode() == null || level.getLevelCode() < 3) {
                    continue; // 只有黄金游侠及以上等级才有优惠券
                }
                
                // 发放优惠券
                int count = distributeCouponsByLevel(
                    userPoints.getUserId(), 
                    level.getLevelCode(), 
                    level.getLevelName());
                
                if (count > 0) {
                    totalDistributed += count;
                    totalUsers++;
                }
            }
            
            System.out.println("✅ 每月优惠券发放完成，共为 " + totalUsers + " 位用户发放了 " + 
                             totalDistributed + " 张优惠券");
        } catch (Exception e) {
            System.err.println("❌ 每月优惠券发放失败: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}









