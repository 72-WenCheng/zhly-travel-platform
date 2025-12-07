package com.zhly.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhly.entity.SearchLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 搜索日志Mapper接口
 */
@Mapper
public interface SearchLogMapper extends BaseMapper<SearchLog> {
    
    /**
     * 获取按国家聚合的搜索统计数据（检索量）
     * 只统计攻略社区和景点社区的搜索
     */
    @Select("""
            SELECT 
                COALESCE(NULLIF(country_code, ''), 'CN') AS countryCode,
                COALESCE(NULLIF(country_name, ''), '中国') AS countryName,
                COUNT(*) AS queries
            FROM search_log
            WHERE search_type IN ('attractions', 'travel-plans')
            GROUP BY 
                COALESCE(NULLIF(country_code, ''), 'CN'),
                COALESCE(NULLIF(country_name, ''), '中国')
            """)
    List<Map<String, Object>> selectSearchStatisticsByCountry();
}













































































