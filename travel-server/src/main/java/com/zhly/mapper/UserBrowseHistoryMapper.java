package com.zhly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhly.entity.UserBrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 用户浏览历史Mapper接口
 */
@Mapper
public interface UserBrowseHistoryMapper extends BaseMapper<UserBrowseHistory> {
    
    /**
     * 获取用户最近浏览记录
     */
    List<UserBrowseHistory> selectRecentByUserId(Long userId, Integer limit);

    /**
     * 获取按国家聚合的访问统计数据（访问量）
     */
    @Select("""
            SELECT 
                COALESCE(NULLIF(country_code, ''), 'CN') AS countryCode,
                COALESCE(NULLIF(country_name, ''), '中国') AS countryName,
                COUNT(*) AS visits,
                AVG(COALESCE(duration, 0)) AS avgDuration
            FROM user_browse_history
            GROUP BY 
                COALESCE(NULLIF(country_code, ''), 'CN'),
                COALESCE(NULLIF(country_name, ''), '中国')
            """)
    List<Map<String, Object>> selectWorldTrafficAggregates();
}







