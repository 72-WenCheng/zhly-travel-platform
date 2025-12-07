package com.zhly.service.impl;

import com.zhly.entity.SearchLog;
import com.zhly.mapper.SearchLogMapper;
import com.zhly.service.SearchService;
import com.zhly.service.AttractionService;
import com.zhly.service.TravelPlanService;
import com.zhly.service.CultureProjectService;
import com.zhly.service.TravelRouteService;
import com.zhly.service.dto.GeoIpInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.time.LocalDateTime;

/**
 * 搜索服务实现类
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Service
public class SearchServiceImpl implements SearchService {
    
    @Autowired
    private AttractionService attractionService;
    
    @Autowired
    private TravelPlanService travelPlanService;
    
    @Autowired
    private CultureProjectService cultureProjectService;
    
    @Autowired
    private TravelRouteService travelRouteService;
    
    @Autowired
    private SearchLogMapper searchLogMapper;
    
    @Autowired
    private com.zhly.service.GeoIpService geoIpService;
    
    @Override
    public Map<String, Object> globalSearch(String keyword, Integer page, Integer size) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 搜索景点
            Map<String, Object> attractions = attractionService.getAttractionList(page, size, keyword, null, null, 1, null, null);
            result.put("attractions", attractions);
            
            // 搜索旅游攻略（只搜索已发布且审核通过的）
            Map<String, Object> travelPlans = travelPlanService.getPlanList(
                page, size, keyword, null, 1, 1, null, null, null, null
            );
            result.put("travelPlans", travelPlans);
            
            // 搜索文旅项目
            Map<String, Object> cultureProjects = cultureProjectService.getProjectList(page, size, keyword, null, null, 1);
            result.put("cultureProjects", cultureProjects);
            
            // 搜索旅游路线
            Map<String, Object> travelRoutes = travelRouteService.getRouteList(page, size, keyword, null, null, 1);
            result.put("travelRoutes", travelRoutes);
            
            return result;
        } catch (Exception e) {
            throw new RuntimeException("全局搜索失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> searchAttractions(String keyword, String city, Integer page, Integer size) {
        try {
            return attractionService.getAttractionList(page, size, keyword, city, null, 1, null, null);
        } catch (Exception e) {
            throw new RuntimeException("搜索景点失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> searchTravelPlans(String keyword, String destination, Integer page, Integer size) {
        try {
            // 只搜索已发布且审核通过的攻略
            return travelPlanService.getPlanList(
                page, size, keyword, destination, 1, 1, null, null, null, null
            );
        } catch (Exception e) {
            throw new RuntimeException("搜索旅游攻略失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> searchCultureProjects(String keyword, String region, Integer page, Integer size) {
        try {
            return cultureProjectService.getProjectList(page, size, keyword, region, null, 1);
        } catch (Exception e) {
            throw new RuntimeException("搜索文旅项目失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> searchTravelRoutes(String keyword, String startCity, String endCity, Integer page, Integer size) {
        try {
            return travelRouteService.getRouteList(page, size, keyword, startCity, endCity, 1);
        } catch (Exception e) {
            throw new RuntimeException("搜索旅游路线失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<String> getSearchSuggestions(String keyword, Integer limit) {
        try {
            // 这里可以实现基于历史搜索、热门搜索等的智能建议
            List<String> suggestions = new ArrayList<>();
            suggestions.add(keyword + " 景点");
            suggestions.add(keyword + " 攻略");
            suggestions.add(keyword + " 路线");
            suggestions.add(keyword + " 项目");
            return suggestions.subList(0, Math.min(limit, suggestions.size()));
        } catch (Exception e) {
            throw new RuntimeException("获取搜索建议失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean recordSearchHistory(Long userId, String keyword, String searchType) {
        try {
            // 记录搜索日志到数据库
            SearchLog searchLog = new SearchLog();
            searchLog.setUserId(userId);
            searchLog.setKeyword(keyword);
            searchLog.setSearchType(searchType);
            searchLog.setResultCount(0); // 结果数量可以在搜索后更新
            searchLog.setSearchTime(LocalDateTime.now());
            
            searchLogMapper.insert(searchLog);
            return true;
        } catch (Exception e) {
            // 记录搜索日志失败不应该影响搜索功能，只记录错误
            System.err.println("记录搜索历史失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 记录搜索日志（带结果数量）
     */
    public void recordSearchLog(Long userId, String keyword, String searchType, Integer resultCount) {
        recordSearchLog(userId, keyword, searchType, resultCount, null);
    }
    
    /**
     * 记录搜索日志（带结果数量和IP信息，用于地理统计）
     */
    public void recordSearchLog(Long userId, String keyword, String searchType, Integer resultCount, String ipAddress) {
        try {
            SearchLog searchLog = new SearchLog();
            searchLog.setUserId(userId);
            searchLog.setKeyword(keyword);
            searchLog.setSearchType(searchType);
            searchLog.setResultCount(resultCount != null ? resultCount : 0);
            searchLog.setSearchTime(LocalDateTime.now());
            searchLog.setIpAddress(ipAddress);
            
            // 优先根据搜索关键词推断国家（如果搜索的是外国城市）
            GeoIpInfo keywordCountryInfo = inferCountryFromKeyword(keyword);
            if (keywordCountryInfo != null) {
                searchLog.setCountryCode(keywordCountryInfo.getCountryCode());
                searchLog.setCountryName(keywordCountryInfo.getCountryName());
            } else {
                // 如果无法从关键词推断，则使用IP地址解析国家信息
            if (ipAddress != null && !ipAddress.trim().isEmpty()) {
                try {
                    GeoIpInfo geoIpInfo = geoIpService.lookup(ipAddress);
                    if (geoIpInfo != null) {
                        searchLog.setCountryCode(geoIpInfo.getCountryCode());
                        searchLog.setCountryName(geoIpInfo.getCountryName());
                        } else {
                            // 如果IP解析失败，设置默认值（中国）
                            searchLog.setCountryCode("CN");
                            searchLog.setCountryName("中国");
                    }
                } catch (Exception e) {
                        // IP解析失败不影响搜索日志记录，设置默认值
                    System.err.println("解析IP地址失败: " + e.getMessage());
                        searchLog.setCountryCode("CN");
                        searchLog.setCountryName("中国");
                    }
                } else {
                    // 如果没有IP地址，设置默认值（中国）
                    searchLog.setCountryCode("CN");
                    searchLog.setCountryName("中国");
                }
            }
            
            searchLogMapper.insert(searchLog);
        } catch (Exception e) {
            // 记录搜索日志失败不应该影响搜索功能，只记录错误
            System.err.println("记录搜索日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 根据搜索关键词推断国家信息
     * 如果搜索的是外国城市，返回该城市所在的国家信息
     */
    private GeoIpInfo inferCountryFromKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return null;
        }
        
        String normalizedKeyword = keyword.trim().toLowerCase();
        
        // 国际城市到国家的映射
        java.util.Map<String, String[]> cityCountryMap = new java.util.HashMap<>();
        
        // 欧洲城市
        cityCountryMap.put("维也纳", new String[]{"AT", "奥地利"});
        cityCountryMap.put("vienna", new String[]{"AT", "Austria"});
        cityCountryMap.put("巴黎", new String[]{"FR", "法国"});
        cityCountryMap.put("paris", new String[]{"FR", "France"});
        cityCountryMap.put("伦敦", new String[]{"GB", "英国"});
        cityCountryMap.put("london", new String[]{"GB", "United Kingdom"});
        cityCountryMap.put("罗马", new String[]{"IT", "意大利"});
        cityCountryMap.put("rome", new String[]{"IT", "Italy"});
        cityCountryMap.put("柏林", new String[]{"DE", "德国"});
        cityCountryMap.put("berlin", new String[]{"DE", "Germany"});
        cityCountryMap.put("马德里", new String[]{"ES", "西班牙"});
        cityCountryMap.put("madrid", new String[]{"ES", "Spain"});
        cityCountryMap.put("阿姆斯特丹", new String[]{"NL", "荷兰"});
        cityCountryMap.put("amsterdam", new String[]{"NL", "Netherlands"});
        cityCountryMap.put("莫斯科", new String[]{"RU", "俄罗斯"});
        cityCountryMap.put("moscow", new String[]{"RU", "Russia"});
        
        // 亚洲城市
        cityCountryMap.put("东京", new String[]{"JP", "日本"});
        cityCountryMap.put("tokyo", new String[]{"JP", "Japan"});
        cityCountryMap.put("首尔", new String[]{"KR", "韩国"});
        cityCountryMap.put("seoul", new String[]{"KR", "South Korea"});
        cityCountryMap.put("新加坡", new String[]{"SG", "新加坡"});
        cityCountryMap.put("singapore", new String[]{"SG", "Singapore"});
        cityCountryMap.put("曼谷", new String[]{"TH", "泰国"});
        cityCountryMap.put("bangkok", new String[]{"TH", "Thailand"});
        
        // 北美洲城市
        cityCountryMap.put("纽约", new String[]{"US", "美国"});
        cityCountryMap.put("new york", new String[]{"US", "United States"});
        cityCountryMap.put("洛杉矶", new String[]{"US", "美国"});
        cityCountryMap.put("los angeles", new String[]{"US", "United States"});
        cityCountryMap.put("温哥华", new String[]{"CA", "加拿大"});
        cityCountryMap.put("vancouver", new String[]{"CA", "Canada"});
        cityCountryMap.put("多伦多", new String[]{"CA", "加拿大"});
        cityCountryMap.put("toronto", new String[]{"CA", "Canada"});
        
        // 大洋洲城市
        cityCountryMap.put("悉尼", new String[]{"AU", "澳大利亚"});
        cityCountryMap.put("sydney", new String[]{"AU", "Australia"});
        cityCountryMap.put("墨尔本", new String[]{"AU", "澳大利亚"});
        cityCountryMap.put("melbourne", new String[]{"AU", "Australia"});
        
        // 检查关键词是否匹配城市
        String[] countryInfo = cityCountryMap.get(normalizedKeyword);
        if (countryInfo == null) {
            // 尝试部分匹配（包含关系）
            for (java.util.Map.Entry<String, String[]> entry : cityCountryMap.entrySet()) {
                if (normalizedKeyword.contains(entry.getKey()) || entry.getKey().contains(normalizedKeyword)) {
                    countryInfo = entry.getValue();
                    break;
                }
            }
        }
        
        if (countryInfo != null) {
            GeoIpInfo info = new GeoIpInfo();
            info.setCountryCode(countryInfo[0]);
            info.setCountryName(countryInfo[1]);
            return info;
        }
        
        return null;
    }
    
    @Override
    public List<Map<String, Object>> getHotSearchWords(Integer limit) {
        try {
            // 实现热门搜索词逻辑 - 可以从数据库或Redis中获取
            List<Map<String, Object>> hotWords = new ArrayList<>();
            
            // 模拟热门搜索词数据
            String[] keywords = {"北京", "上海", "杭州", "成都", "西安", "青岛", "厦门", "三亚", "桂林", "丽江"};
            int[] counts = {1000, 800, 600, 500, 400, 350, 300, 250, 200, 150};
            
            for (int i = 0; i < Math.min(keywords.length, limit); i++) {
                Map<String, Object> word = new HashMap<>();
                word.put("keyword", keywords[i]);
                word.put("count", counts[i]);
                word.put("trend", i < 3 ? "up" : "stable"); // 前3个显示上升趋势
                hotWords.add(word);
            }
            
            return hotWords;
        } catch (Exception e) {
            throw new RuntimeException("获取热门搜索词失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<String> getUserSearchHistory(Long userId, Integer limit) {
        try {
            // 实现用户搜索历史逻辑 - 可以从Redis或数据库中获取
            List<String> history = new ArrayList<>();
            
            // 模拟用户搜索历史数据
            String[] searchHistory = {
                "北京旅游", "上海美食", "杭州西湖", "成都火锅", "西安兵马俑", 
                "青岛啤酒", "厦门鼓浪屿", "三亚海滩", "桂林山水", "丽江古城"
            };
            
            // 根据用户ID生成不同的历史记录
            int startIndex = (int) (userId % searchHistory.length);
            for (int i = 0; i < Math.min(limit, searchHistory.length); i++) {
                int index = (startIndex + i) % searchHistory.length;
                history.add(searchHistory[index]);
            }
            
            return history;
        } catch (Exception e) {
            throw new RuntimeException("获取用户搜索历史失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean clearUserSearchHistory(Long userId) {
        try {
            // 实现清除用户搜索历史逻辑
            // 可以从Redis或数据库中删除用户的搜索历史
            System.out.println("清除用户搜索历史: 用户ID=" + userId);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("清除用户搜索历史失败: " + e.getMessage());
        }
    }
}

