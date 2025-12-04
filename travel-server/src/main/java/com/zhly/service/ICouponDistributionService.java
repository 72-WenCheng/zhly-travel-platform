package com.zhly.service;

/**
 * 优惠券发放服务接口
 * 
 * @author zhly
 * @since 2025-01-01
 */
public interface ICouponDistributionService {
    
    /**
     * 根据用户等级发放优惠券
     * @param userId 用户ID
     * @param levelCode 等级代码（3-黄金游侠，4-铂金旅者，5-钻石达人，6-王者导师）
     * @param levelName 等级名称
     * @return 发放的优惠券数量
     */
    int distributeCouponsByLevel(Long userId, Integer levelCode, String levelName);
    
    /**
     * 每月自动发放优惠券（定时任务调用）
     */
    void distributeMonthlyCoupons();
}



