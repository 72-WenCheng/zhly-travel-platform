package com.zhly.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhly.entity.*;
import com.zhly.mapper.*;
import com.zhly.user.service.UserPortraitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户画像服务实现类
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Service
public class UserPortraitServiceImpl implements UserPortraitService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserBrowseHistoryMapper browseHistoryMapper;
    
    @Autowired
    private UserCollectMapper userCollectMapper;
    
    @Autowired
    private AttractionMapper attractionMapper;
    
    @Autowired
    private TravelPlanMapper travelPlanMapper;
    
    @Autowired
    private SearchLogMapper searchLogMapper;
    
    @Override
    public Map<String, Object> getUserPortrait(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取用户基本信息
            User user = userMapper.selectById(userId);
            if (user == null) {
                // 用户不存在，返回空数据（不返回默认值）
                return buildEmptyPortrait();
            }
            
            // 2. 统计数据
            Long browseCount = getBrowseCount(userId);
            Long favoriteCount = getFavoriteCount(userId);
            Long searchCount = getSearchCount(userId);
            Integer avgStayTime = getAvgStayTime(userId);
            
            // 3. 旅游偏好分析
            Map<String, Object> preferenceAnalysis = analyzeTravelPreference(userId);
            
            // 4. 兴趣标签云
            List<Map<String, Object>> interestTags = analyzeInterestTags(userId);
            
            // 5. 消费行为分析
            Map<String, Object> consumptionAnalysis = analyzeConsumptionBehavior(userId);
            
            // 6. 出行特征
            Map<String, Object> travelCharacteristics = analyzeTravelCharacteristics(userId, user);
            
            // 7. 组装结果
            result.put("browseCount", browseCount);
            result.put("favoriteCount", favoriteCount);
            result.put("searchCount", searchCount);
            result.put("avgStayTime", avgStayTime);
            result.put("primaryPreference", preferenceAnalysis.get("primaryPreference"));
            result.put("preferenceDistribution", preferenceAnalysis.get("distribution"));
            result.put("interestTags", interestTags);
            result.put("consumptionLevel", consumptionAnalysis.get("level"));
            result.put("avgConsumption", consumptionAnalysis.get("avgConsumption"));
            result.put("consumptionFrequency", consumptionAnalysis.get("frequency"));
            result.put("priceSensitivity", consumptionAnalysis.get("priceSensitivity"));
            result.put("travelMode", travelCharacteristics.get("travelMode"));
            result.put("tripDuration", travelCharacteristics.get("tripDuration"));
            result.put("seasonPreference", travelCharacteristics.get("seasonPreference"));
            result.put("frequentDestinations", travelCharacteristics.get("frequentDestinations"));
            result.put("lastUpdateTime", LocalDateTime.now().toString().replace("T", " ").substring(0, 16));
            
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            // 发生异常，返回空数据（不返回默认值）
            return buildEmptyPortrait();
        }
    }
    
    /**
     * 获取浏览次数
     */
    private Long getBrowseCount(Long userId) {
        QueryWrapper<UserBrowseHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return browseHistoryMapper.selectCount(wrapper);
    }
    
    /**
     * 获取收藏数量
     */
    private Long getFavoriteCount(Long userId) {
        QueryWrapper<UserCollect> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return userCollectMapper.selectCount(wrapper);
    }
    
    /**
     * 获取搜索次数（从搜索日志表统计）
     */
    private Long getSearchCount(Long userId) {
        try {
            // 从搜索日志表统计真实的搜索次数
            QueryWrapper<SearchLog> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            return searchLogMapper.selectCount(wrapper);
        } catch (Exception e) {
            // 如果搜索日志表不存在或查询失败，返回0（不返回估算值）
            System.err.println("获取搜索次数失败: " + e.getMessage());
            return 0L;
        }
    }
    
    /**
     * 获取平均停留时间（分钟）
     */
    private Integer getAvgStayTime(Long userId) {
        QueryWrapper<UserBrowseHistory> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.select("duration");
        wrapper.isNotNull("duration"); // 只统计有duration的记录
        wrapper.gt("duration", 0); // duration必须大于0
        List<UserBrowseHistory> histories = browseHistoryMapper.selectList(wrapper);
        
        // 如果没有有效的停留时间记录，返回0（表示没有数据）
        if (histories == null || histories.isEmpty()) {
            return 0;
        }
        
        // 计算平均停留时间（秒）
        int totalDuration = histories.stream()
            .mapToInt(h -> h.getDuration() != null ? h.getDuration() : 0)
            .sum();
        
        int avgSeconds = totalDuration / histories.size();
        
        // 转换为分钟，如果小于1分钟则设为1分钟，如果大于180分钟（3小时）则设为180分钟
        int avgMinutes = Math.max(1, avgSeconds / 60);
        return Math.min(avgMinutes, 180); // 最大180分钟（3小时）
    }
    
    /**
     * 分析旅游偏好
     * 
     * 优化说明：
     * 1. 统计景点类型（基于景点的type字段）和攻略标签（基于攻略的tags字段）
     * 2. 浏览和收藏都统计，权重相同（因为都是反映用户偏好）
     * 3. 攻略标签映射到景点类型，统一统计口径
     */
    private Map<String, Object> analyzeTravelPreference(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 统计用户浏览和收藏的景点类型分布
        Map<String, Integer> typeCountMap = new HashMap<>();
        
        // 1. 从浏览历史分析
        QueryWrapper<UserBrowseHistory> browseWrapper = new QueryWrapper<>();
        browseWrapper.eq("user_id", userId);
        List<UserBrowseHistory> browseHistories = browseHistoryMapper.selectList(browseWrapper);
        
        for (UserBrowseHistory history : browseHistories) {
            if (history.getBrowseType() == 1) { // 景点
                Attraction attraction = attractionMapper.selectById(history.getBrowseId());
                if (attraction != null && attraction.getType() != null) {
                    String typeName = getAttractionTypeName(attraction.getType());
                    typeCountMap.put(typeName, typeCountMap.getOrDefault(typeName, 0) + 1);
                }
            } else if (history.getBrowseType() == 2) { // 攻略
                TravelPlan plan = travelPlanMapper.selectById(history.getBrowseId());
                if (plan != null && plan.getTags() != null) {
                    // 从标签中提取偏好（映射到景点类型）
                    String[] tags = plan.getTags().split(",");
                    for (String tag : tags) {
                        String preference = mapTagToPreference(tag.trim());
                        if (preference != null) {
                            typeCountMap.put(preference, typeCountMap.getOrDefault(preference, 0) + 1);
                        }
                    }
                }
            }
        }
        
        // 2. 从收藏记录分析（包括景点和攻略）
        QueryWrapper<UserCollect> collectWrapper = new QueryWrapper<>();
        collectWrapper.eq("user_id", userId);
        List<UserCollect> collects = userCollectMapper.selectList(collectWrapper);
        
        for (UserCollect collect : collects) {
            if (collect.getCollectType() == 1) { // 攻略收藏（之前缺失，现在补充）
                TravelPlan plan = travelPlanMapper.selectById(collect.getCollectId());
                if (plan != null && plan.getTags() != null) {
                    // 从标签中提取偏好（映射到景点类型）
                    String[] tags = plan.getTags().split(",");
                    for (String tag : tags) {
                        String preference = mapTagToPreference(tag.trim());
                        if (preference != null) {
                            typeCountMap.put(preference, typeCountMap.getOrDefault(preference, 0) + 1);
                        }
                    }
                }
            } else if (collect.getCollectType() == 2) { // 景点收藏
                Attraction attraction = attractionMapper.selectById(collect.getCollectId());
                if (attraction != null && attraction.getType() != null) {
                    String typeName = getAttractionTypeName(attraction.getType());
                    typeCountMap.put(typeName, typeCountMap.getOrDefault(typeName, 0) + 1);
                }
            }
        }
        
        // 如果没有任何数据，返回空数据
        if (typeCountMap.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("primaryPreference", "");
            emptyResult.put("distribution", new ArrayList<>());
            return emptyResult;
        }
        
        // 计算百分比
        int total = typeCountMap.values().stream().mapToInt(Integer::intValue).sum();
        List<Map<String, Object>> distribution = new ArrayList<>();
        
        // 定义偏好类型（与管理平台创建景点的类型对应）
        Map<String, String> preferenceMap = new HashMap<>();
        preferenceMap.put("自然风光", "🏞️");  // type=1
        preferenceMap.put("人文历史", "🏛️");  // type=2
        preferenceMap.put("主题公园", "🎢");  // type=3
        preferenceMap.put("美食街", "🍜");    // type=4
        preferenceMap.put("古镇古村", "🏘️");  // type=5
        preferenceMap.put("温泉度假", "♨️");   // type=6
        preferenceMap.put("宗教场所", "⛩️");   // type=7
        preferenceMap.put("购物娱乐", "🛍️");   // type=8
        
        // 合并类型到偏好分类（统一为8种景点类型）
        Map<String, Integer> mergedMap = new HashMap<>();
        mergedMap.put("自然风光", 0);      // type=1
        mergedMap.put("人文历史", 0);      // type=2
        mergedMap.put("主题公园", 0);      // type=3
        mergedMap.put("美食街", 0);        // type=4
        mergedMap.put("古镇古村", 0);      // type=5
        mergedMap.put("温泉度假", 0);      // type=6
        mergedMap.put("宗教场所", 0);      // type=7
        mergedMap.put("购物娱乐", 0);      // type=8
        
        // 合并统计（直接匹配或关键词匹配）
        for (Map.Entry<String, Integer> entry : typeCountMap.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();
            
            // 直接匹配标准类型名称
            if (mergedMap.containsKey(key)) {
                mergedMap.put(key, mergedMap.get(key) + value);
            } else {
                // 关键词匹配（兼容旧数据或非标准类型名称）
                String matchedType = matchPreferenceType(key);
                if (matchedType != null) {
                    mergedMap.put(matchedType, mergedMap.get(matchedType) + value);
                }
            }
        }
        
        // 如果合并后还是空的，返回空数据
        if (mergedMap.values().stream().allMatch(v -> v == 0)) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("primaryPreference", "");
            emptyResult.put("distribution", new ArrayList<>());
            return emptyResult;
        }
        
        // 计算百分比并排序
        List<Map.Entry<String, Integer>> sorted = mergedMap.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .collect(Collectors.toList());
        
        String primaryPreference = sorted.get(0).getKey();
        
        // 计算百分比
        for (Map.Entry<String, Integer> entry : sorted) {
            if (entry.getValue() > 0) {
                int percentage = (int) Math.round((entry.getValue() * 100.0) / total);
                Map<String, Object> item = new HashMap<>();
                item.put("type", entry.getKey());
                item.put("icon", preferenceMap.getOrDefault(entry.getKey(), "📍"));
                item.put("percentage", percentage);
                item.put("color", getPreferenceColor(entry.getKey()));
                distribution.add(item);
            }
        }
        
        // 确保百分比总和为100
        int sum = distribution.stream().mapToInt(d -> (Integer) d.get("percentage")).sum();
        if (sum != 100 && !distribution.isEmpty()) {
            int diff = 100 - sum;
            Map<String, Object> first = distribution.get(0);
            first.put("percentage", (Integer) first.get("percentage") + diff);
        }
        
        result.put("primaryPreference", primaryPreference);
        result.put("distribution", distribution);
        
        return result;
    }
    
    /**
     * 分析兴趣标签云
     * 
     * 优化说明：
     * 1. 统计景点和攻略的tags字段（浏览和收藏都统计）
     * 2. 浏览权重(2) > 收藏权重(1)，浏览更能反映用户兴趣广度
     * 3. 将用户标签映射到标准标签，统一展示
     */
    private List<Map<String, Object>> analyzeInterestTags(Long userId) {
        Map<String, Integer> tagWeightMap = new HashMap<>();
        
        // 从浏览历史提取标签
        QueryWrapper<UserBrowseHistory> browseWrapper = new QueryWrapper<>();
        browseWrapper.eq("user_id", userId);
        List<UserBrowseHistory> browseHistories = browseHistoryMapper.selectList(browseWrapper);
        
        for (UserBrowseHistory history : browseHistories) {
            if (history.getBrowseType() == 1) { // 景点
                Attraction attraction = attractionMapper.selectById(history.getBrowseId());
                if (attraction != null && attraction.getTags() != null) {
                    String[] tags = attraction.getTags().split(",");
                    for (String tag : tags) {
                        // 去除前后空格，并去除#号等特殊字符
                        String trimmed = tag.trim().replace("#", "").replace("@", "");
                        if (!trimmed.isEmpty()) {
                            tagWeightMap.put(trimmed, tagWeightMap.getOrDefault(trimmed, 0) + 2); // 浏览权重为2
                        }
                    }
                }
            } else if (history.getBrowseType() == 2) { // 攻略
                TravelPlan plan = travelPlanMapper.selectById(history.getBrowseId());
                if (plan != null && plan.getTags() != null) {
                    String[] tags = plan.getTags().split(",");
                    for (String tag : tags) {
                        // 去除前后空格，并去除#号等特殊字符
                        String trimmed = tag.trim().replace("#", "").replace("@", "");
                        if (!trimmed.isEmpty()) {
                            tagWeightMap.put(trimmed, tagWeightMap.getOrDefault(trimmed, 0) + 2); // 浏览权重为2
                        }
                    }
                }
            }
        }
        
        // 从收藏记录提取标签
        QueryWrapper<UserCollect> collectWrapper = new QueryWrapper<>();
        collectWrapper.eq("user_id", userId);
        List<UserCollect> collects = userCollectMapper.selectList(collectWrapper);
        
        for (UserCollect collect : collects) {
            if (collect.getCollectType() == 1) { // 攻略
                TravelPlan plan = travelPlanMapper.selectById(collect.getCollectId());
                if (plan != null && plan.getTags() != null) {
                    String[] tags = plan.getTags().split(",");
                    for (String tag : tags) {
                        // 去除前后空格，并去除#号等特殊字符
                        String trimmed = tag.trim().replace("#", "").replace("@", "");
                        if (!trimmed.isEmpty()) {
                            tagWeightMap.put(trimmed, tagWeightMap.getOrDefault(trimmed, 0) + 1); // 收藏权重为1（低于浏览）
                        }
                    }
                }
            } else if (collect.getCollectType() == 2) { // 景点
                Attraction attraction = attractionMapper.selectById(collect.getCollectId());
                if (attraction != null && attraction.getTags() != null) {
                    String[] tags = attraction.getTags().split(",");
                    for (String tag : tags) {
                        // 去除前后空格，并去除#号等特殊字符
                        String trimmed = tag.trim().replace("#", "").replace("@", "");
                        if (!trimmed.isEmpty()) {
                            tagWeightMap.put(trimmed, tagWeightMap.getOrDefault(trimmed, 0) + 1); // 收藏权重为1（低于浏览）
                        }
                    }
                }
            }
        }
        
        // 如果没有数据，返回空列表
        if (tagWeightMap.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 转换为标准兴趣标签（定义标准标签及其图标）
        Map<String, String> standardTags = new HashMap<>();
        standardTags.put("摄影", "📷");
        standardTags.put("美食", "🍜");
        standardTags.put("文化", "🎭");
        standardTags.put("自然风光", "🏔️");
        standardTags.put("历史", "📜");
        standardTags.put("建筑", "🏛️");
        standardTags.put("徒步", "🥾");
        standardTags.put("温泉", "♨️");
        standardTags.put("博物馆", "🏛️");
        standardTags.put("夜生活", "🌙");
        standardTags.put("夜景", "🌙"); // 夜景作为独立标签
        standardTags.put("购物", "🛍️");
        standardTags.put("度假", "🏖️");
        standardTags.put("情侣", "💑");
        standardTags.put("亲子", "👨‍👩‍👧"); // 添加亲子标签
        standardTags.put("休闲", "🏖️"); // 休闲映射到度假
        
        // 映射用户标签到标准标签
        Map<String, Integer> standardWeightMap = new HashMap<>();
        for (Map.Entry<String, Integer> entry : tagWeightMap.entrySet()) {
            String userTag = entry.getKey();
            Integer weight = entry.getValue();
            
            // 首先检查是否直接匹配标准标签（精确匹配优先）
            if (standardTags.containsKey(userTag)) {
                standardWeightMap.put(userTag, standardWeightMap.getOrDefault(userTag, 0) + weight);
            } else {
                // 如果不直接匹配，则进行关键词映射
                String mappedTag = mapTagToStandardTag(userTag);
                if (mappedTag != null) {
                    standardWeightMap.put(mappedTag, standardWeightMap.getOrDefault(mappedTag, 0) + weight);
                } else if (!userTag.isEmpty()) {
                    // 如果没有匹配到任何标准标签，但标签不为空，也保留原始标签（使用默认图标）
                    standardWeightMap.put(userTag, standardWeightMap.getOrDefault(userTag, 0) + weight);
                }
            }
        }
        
        // 如果没有匹配到标准标签，返回空列表
        if (standardWeightMap.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 计算权重并排序
        int maxWeight = standardWeightMap.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : standardWeightMap.entrySet()) {
            Map<String, Object> tag = new HashMap<>();
            tag.put("name", entry.getKey());
            tag.put("icon", standardTags.getOrDefault(entry.getKey(), "📍"));
            // 权重归一化到1-10
            tag.put("weight", Math.max(1, Math.min(10, (int) Math.round(entry.getValue() * 10.0 / maxWeight))));
            result.add(tag);
        }
        
        // 按权重排序
        result.sort((a, b) -> ((Integer) b.get("weight")).compareTo((Integer) a.get("weight")));
        
        // 限制返回数量
        return result.stream().limit(10).collect(Collectors.toList());
    }
    
    /**
     * 分析消费行为
     * 
     * 优化说明：
     * 1. 主要基于攻略的日均预算计算平均消费（更准确反映实际出行消费水平）
     * 2. 景点门票价格作为辅助参考（转换为日均消费估算：门票价格 × 3，假设一天去3个景点）
     * 3. 收藏权重(2) > 浏览权重(1)，收藏更能反映真实消费意愿
     */
    private Map<String, Object> analyzeConsumptionBehavior(Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 使用Set去重浏览记录，key为"browseType-browseId"
        Set<String> browseKeys = new HashSet<>();
        Map<String, BigDecimal> priceMap = new HashMap<>(); // key为"browseType-browseId"，value为日均价格（元/天）
        Map<String, Integer> weightMap = new HashMap<>(); // key为"browseType-browseId"，value为权重（1=浏览，2=收藏）
        
        // 1. 从用户浏览的攻略中分析消费水平（主要数据源）
        QueryWrapper<UserBrowseHistory> browseWrapper = new QueryWrapper<>();
        browseWrapper.eq("user_id", userId);
        browseWrapper.eq("browse_type", 2); // 只统计攻略
        List<UserBrowseHistory> planHistories = browseHistoryMapper.selectList(browseWrapper);
        
        for (UserBrowseHistory history : planHistories) {
            String key = history.getBrowseType() + "-" + history.getBrowseId();
            // 去重：同一个攻略只统计一次
            if (browseKeys.contains(key)) {
                continue;
            }
            browseKeys.add(key);
            
            TravelPlan plan = travelPlanMapper.selectById(history.getBrowseId());
            if (plan != null && plan.getBudget() != null 
                && plan.getBudget().compareTo(BigDecimal.ZERO) > 0) {
                // 攻略预算按天数平均，得到日均预算（单位：元/天）
                BigDecimal avgBudget = plan.getBudget();
                if (plan.getDays() != null && plan.getDays() > 0) {
                    avgBudget = avgBudget.divide(BigDecimal.valueOf(plan.getDays()), 2, RoundingMode.HALF_UP);
                }
                // 只统计大于0的预算
                if (avgBudget.compareTo(BigDecimal.ZERO) > 0) {
                    priceMap.put(key, avgBudget);
                    weightMap.put(key, 1); // 浏览权重为1
                }
            }
        }
        
        // 2. 从用户浏览的景点中分析消费水平（辅助数据源）
        // 景点门票价格转换为日均消费估算：假设用户一天去3个景点，日均消费 = 门票价格 × 3
        QueryWrapper<UserBrowseHistory> attractionBrowseWrapper = new QueryWrapper<>();
        attractionBrowseWrapper.eq("user_id", userId);
        attractionBrowseWrapper.eq("browse_type", 1); // 景点
        List<UserBrowseHistory> attractionHistories = browseHistoryMapper.selectList(attractionBrowseWrapper);
        
        for (UserBrowseHistory history : attractionHistories) {
            String key = history.getBrowseType() + "-" + history.getBrowseId();
            if (browseKeys.contains(key)) {
                continue;
            }
            browseKeys.add(key);
            
            Attraction attraction = attractionMapper.selectById(history.getBrowseId());
            if (attraction != null && attraction.getTicketPrice() != null 
                && attraction.getTicketPrice().compareTo(BigDecimal.ZERO) > 0) {
                // 将景点门票价格转换为日均消费估算：门票价格 × 3（假设一天去3个景点）
                BigDecimal estimatedDailyConsumption = attraction.getTicketPrice().multiply(BigDecimal.valueOf(3));
                priceMap.put(key, estimatedDailyConsumption);
                weightMap.put(key, 1); // 浏览权重为1（景点数据权重较低）
            }
        }
        
        // 3. 从收藏记录中分析消费水平（收藏权重更高，覆盖浏览记录）
        QueryWrapper<UserCollect> collectWrapper = new QueryWrapper<>();
        collectWrapper.eq("user_id", userId);
        List<UserCollect> collects = userCollectMapper.selectList(collectWrapper);
        
        for (UserCollect collect : collects) {
            String key = collect.getCollectType() + "-" + collect.getCollectId();
            
            if (collect.getCollectType() == 1) { // 攻略收藏（主要数据源）
                TravelPlan plan = travelPlanMapper.selectById(collect.getCollectId());
                if (plan != null && plan.getBudget() != null 
                    && plan.getBudget().compareTo(BigDecimal.ZERO) > 0) {
                    // 攻略预算按天数平均，得到日均预算（单位：元/天）
                    BigDecimal avgBudget = plan.getBudget();
                    if (plan.getDays() != null && plan.getDays() > 0) {
                        avgBudget = avgBudget.divide(BigDecimal.valueOf(plan.getDays()), 2, RoundingMode.HALF_UP);
                    }
                    // 只统计大于0的预算
                    if (avgBudget.compareTo(BigDecimal.ZERO) > 0) {
                        // 收藏的攻略预算覆盖浏览记录，权重更高
                        priceMap.put(key, avgBudget);
                        weightMap.put(key, 2); // 收藏权重为2
                    }
                }
            } else if (collect.getCollectType() == 2) { // 景点收藏（辅助数据源）
                Attraction attraction = attractionMapper.selectById(collect.getCollectId());
                if (attraction != null && attraction.getTicketPrice() != null 
                    && attraction.getTicketPrice().compareTo(BigDecimal.ZERO) > 0) {
                    // 将景点门票价格转换为日均消费估算：门票价格 × 3
                    BigDecimal estimatedDailyConsumption = attraction.getTicketPrice().multiply(BigDecimal.valueOf(3));
                    priceMap.put(key, estimatedDailyConsumption);
                    weightMap.put(key, 2); // 收藏权重为2（但景点数据权重仍较低）
                }
            }
        }
        
        // 计算加权平均消费（根据权重计算，单位：元/天）
        BigDecimal avgConsumption = BigDecimal.ZERO;
        BigDecimal totalWeightedSum = BigDecimal.ZERO;
        int totalWeight = 0;
        
        for (Map.Entry<String, BigDecimal> entry : priceMap.entrySet()) {
            String key = entry.getKey();
            BigDecimal price = entry.getValue();
            Integer weight = weightMap.getOrDefault(key, 1);
            
            // 加权求和：价格 × 权重
            totalWeightedSum = totalWeightedSum.add(price.multiply(BigDecimal.valueOf(weight)));
            totalWeight += weight;
        }
        
        // 计算加权平均值
        if (totalWeight > 0) {
            avgConsumption = totalWeightedSum.divide(BigDecimal.valueOf(totalWeight), 2, RoundingMode.HALF_UP);
        }
        
        // 如果没有数据，平均消费为0（不设置默认值）
        
        // 判断消费水平（如果没有数据，返回空字符串）
        String consumptionLevel = "";
        String levelIcon = "";
        String levelColor = "";
        
        if (avgConsumption.compareTo(BigDecimal.ZERO) > 0) {
            if (avgConsumption.compareTo(BigDecimal.valueOf(1500)) >= 0) {
                consumptionLevel = "豪华型";
                levelIcon = "💎";
                levelColor = "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)";
            } else if (avgConsumption.compareTo(BigDecimal.valueOf(500)) >= 0) {
                consumptionLevel = "品质舒适型";
                levelIcon = "💳";
                levelColor = "linear-gradient(135deg, #667eea 0%, #764ba2 100%)";
            } else {
                consumptionLevel = "经济实惠型";
                levelIcon = "💰";
                levelColor = "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)";
            }
        }
        
        // 消费频次计算（基于最近一个月的活跃行为）
        // 策略：主要基于收藏记录（更能反映真实出行意愿），浏览记录作为辅助
        QueryWrapper<UserCollect> collectFreqWrapper = new QueryWrapper<>();
        collectFreqWrapper.eq("user_id", userId);
        collectFreqWrapper.ge("create_time", LocalDateTime.now().minusMonths(1));
        Long recentCollects = userCollectMapper.selectCount(collectFreqWrapper);
        int collectCount = recentCollects != null ? recentCollects.intValue() : 0;
        
        // 统计最近一个月的浏览记录（按日期去重）
        QueryWrapper<UserBrowseHistory> freqWrapper = new QueryWrapper<>();
        freqWrapper.eq("user_id", userId);
        freqWrapper.ge("create_time", LocalDateTime.now().minusMonths(1));
        List<UserBrowseHistory> recentHistories = browseHistoryMapper.selectList(freqWrapper);
        
        Set<String> activeDays = new HashSet<>();
        if (recentHistories != null && !recentHistories.isEmpty()) {
            for (UserBrowseHistory history : recentHistories) {
                if (history.getCreateTime() != null) {
                    String dayKey = history.getCreateTime().toLocalDate().toString();
                    activeDays.add(dayKey);
                }
            }
        }
        int activeDayCount = activeDays.size();
        
        // 消费频次判断（如果没有数据，返回空字符串）
        String frequency = "";
        if (activeDayCount == 0 && collectCount == 0) {
            frequency = ""; // 没有数据，返回空字符串
        } else {
            // 计算综合活跃度：
            // - 收藏数直接反映出行意愿（1个收藏 ≈ 1次出行计划）
            // - 浏览天数反映关注频率（但浏览不等于实际出行）
            // 综合公式：出行意愿 = 收藏数 + 浏览天数 × 0.3（浏览权重较低）
            double travelIntention = collectCount + activeDayCount * 0.3;
            
            // 根据出行意愿判断消费频次（一个月约30天）
            // 收藏数 >= 4：说明有明确的出行计划，每月至少1-2次
            // 收藏数 >= 2：说明偶尔有出行计划，每月1次左右
            // 收藏数 < 2 但浏览天数多：说明在关注但未确定，偶尔出行
            if (collectCount >= 4) {
                frequency = "每月3-4次";
            } else if (collectCount >= 2) {
                frequency = "每月1-2次";
            } else if (travelIntention >= 4) {
                frequency = "每月1-2次";
            } else if (travelIntention >= 2) {
                frequency = "每月1次";
            } else {
                frequency = "偶尔出行";
            }
        }
        
        // 价格敏感度判断（基于平均消费水平）
        // 逻辑：消费水平越高，价格敏感度越低
        String priceSensitivity = "";
        if (avgConsumption.compareTo(BigDecimal.ZERO) > 0) {
            if (avgConsumption.compareTo(BigDecimal.valueOf(500)) < 0) {
                priceSensitivity = "高"; // 平均消费 < 500元/天，对价格敏感
            } else if (avgConsumption.compareTo(BigDecimal.valueOf(1000)) < 0) {
                priceSensitivity = "中等"; // 平均消费 500-1000元/天，价格敏感度中等
            } else {
                priceSensitivity = "低"; // 平均消费 >= 1000元/天，对价格不敏感
            }
        }
        
        Map<String, Object> level = new HashMap<>();
        level.put("level", consumptionLevel);
        level.put("icon", levelIcon);
        level.put("color", levelColor);
        
        result.put("level", level);
        result.put("avgConsumption", avgConsumption.intValue());
        result.put("frequency", frequency);
        result.put("priceSensitivity", priceSensitivity);
        
        return result;
    }
    
    /**
     * 分析出行特征
     * 
     * 优化说明：
     * 1. 统计浏览和收藏的攻略/景点（收藏更能反映真实偏好）
     * 2. 提取公共方法，减少代码重复
     * 3. 优化城市名提取逻辑
     */
    private Map<String, Object> analyzeTravelCharacteristics(Long userId, User user) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 出行方式分析（统计浏览和收藏的攻略）
        String travelMode = analyzeTravelMode(userId);
        
        // 2. 旅游时长偏好（统计浏览和收藏的攻略）
        String tripDuration = analyzeTripDuration(userId);
        
        // 3. 季节偏好（统计浏览和收藏的攻略和景点）
        String seasonPreference = analyzeSeasonPreference(userId);
        
        // 4. 常去目的地（统计浏览和收藏的攻略和景点）
        String frequentDestinations = analyzeFrequentDestinations(userId);
        
        result.put("travelMode", travelMode);
        result.put("tripDuration", tripDuration);
        result.put("seasonPreference", seasonPreference);
        result.put("frequentDestinations", frequentDestinations);
        
        return result;
    }
    
    /**
     * 分析出行方式（基于攻略的travelType字段）
     * 支持数字格式（1=自由行，2=跟团游，3=自驾游，4=背包客）和字符串格式
     */
    private String analyzeTravelMode(Long userId) {
        Set<String> planKeys = new HashSet<>();
        int[] counts = new int[4]; // [自由行, 跟团游, 自驾游, 背包客]
        
        // 统计浏览的攻略
        QueryWrapper<UserBrowseHistory> browseWrapper = new QueryWrapper<>();
        browseWrapper.eq("user_id", userId);
        browseWrapper.eq("browse_type", 2); // 攻略
        List<UserBrowseHistory> planHistories = browseHistoryMapper.selectList(browseWrapper);
        
        for (UserBrowseHistory history : planHistories) {
            String key = "plan-" + history.getBrowseId();
            if (planKeys.contains(key)) continue;
            planKeys.add(key);
            
            TravelPlan plan = travelPlanMapper.selectById(history.getBrowseId());
            if (plan != null && plan.getTravelType() != null) {
                countTravelType(plan.getTravelType(), counts);
            }
        }
        
        // 统计收藏的攻略
        QueryWrapper<UserCollect> collectWrapper = new QueryWrapper<>();
        collectWrapper.eq("user_id", userId);
        collectWrapper.eq("collect_type", 1); // 攻略
        List<UserCollect> planCollects = userCollectMapper.selectList(collectWrapper);
        
        for (UserCollect collect : planCollects) {
            String key = "plan-" + collect.getCollectId();
            if (planKeys.contains(key)) continue;
            planKeys.add(key);
            
            TravelPlan plan = travelPlanMapper.selectById(collect.getCollectId());
            if (plan != null && plan.getTravelType() != null) {
                countTravelType(plan.getTravelType(), counts);
            }
        }
        
        int freeTravelCount = counts[0];      // 自由行
        int groupTravelCount = counts[1];     // 跟团游
        int selfDriveCount = counts[2];      // 自驾游
        int backpackerCount = counts[3];     // 背包客
        
        // 判断结果（优先显示数量最多的类型）
        int maxCount = Math.max(Math.max(freeTravelCount, selfDriveCount), Math.max(groupTravelCount, backpackerCount));
        if (maxCount == 0) {
            return "";
        }
        
        // 如果自由行和自驾游都有，合并显示
        if (freeTravelCount > 0 && selfDriveCount > 0) {
            return "自由行 + 自驾游";
        } else if (freeTravelCount > 0) {
            return "自由行";
        } else if (selfDriveCount > 0) {
            return "自驾游";
        } else if (groupTravelCount > 0) {
            return "跟团游";
        } else if (backpackerCount > 0) {
            return "背包客";
        }
        
        return "";
    }
    
    /**
     * 统计出行方式（辅助方法，支持数字和字符串格式）
     * counts数组：[自由行, 跟团游, 自驾游, 背包客]
     */
    private void countTravelType(String travelType, int[] counts) {
        if (travelType == null || travelType.trim().isEmpty()) {
            return;
        }
        
        String type = travelType.trim();
        
        // 处理数字格式：1=自由行，2=跟团游，3=自驾游，4=背包客
        try {
            int typeNum = Integer.parseInt(type);
            switch (typeNum) {
                case 1:
                    counts[0]++; // 自由行
                    return;
                case 2:
                    counts[1]++; // 跟团游
                    return;
                case 3:
                    counts[2]++; // 自驾游
                    return;
                case 4:
                    counts[3]++; // 背包客
                    return;
            }
        } catch (NumberFormatException e) {
            // 不是数字，继续处理字符串格式
        }
        
        // 处理字符串格式
        if (type.contains("自由") || type.equals("自由行")) {
            counts[0]++; // 自由行
        } else if (type.contains("跟团") || type.contains("团队") || type.equals("跟团游")) {
            counts[1]++; // 跟团游
        } else if (type.contains("自驾") || type.equals("自驾游")) {
            counts[2]++; // 自驾游
        } else if (type.contains("背包") || type.equals("背包客")) {
            counts[3]++; // 背包客
        }
    }
    
    /**
     * 分析旅游时长偏好（基于攻略的days字段）
     */
    private String analyzeTripDuration(Long userId) {
        Set<String> planKeys = new HashSet<>();
        List<Integer> daysList = new ArrayList<>();
        
        // 统计浏览的攻略
        QueryWrapper<UserBrowseHistory> browseWrapper = new QueryWrapper<>();
        browseWrapper.eq("user_id", userId);
        browseWrapper.eq("browse_type", 2); // 攻略
        List<UserBrowseHistory> planHistories = browseHistoryMapper.selectList(browseWrapper);
        
        for (UserBrowseHistory history : planHistories) {
            String key = "plan-" + history.getBrowseId();
            if (planKeys.contains(key)) continue;
            planKeys.add(key);
            
            TravelPlan plan = travelPlanMapper.selectById(history.getBrowseId());
            if (plan != null && plan.getDays() != null && plan.getDays() > 0) {
                daysList.add(plan.getDays());
            }
        }
        
        // 统计收藏的攻略
        QueryWrapper<UserCollect> collectWrapper = new QueryWrapper<>();
        collectWrapper.eq("user_id", userId);
        collectWrapper.eq("collect_type", 1); // 攻略
        List<UserCollect> planCollects = userCollectMapper.selectList(collectWrapper);
        
        for (UserCollect collect : planCollects) {
            String key = "plan-" + collect.getCollectId();
            if (planKeys.contains(key)) continue;
            planKeys.add(key);
            
            TravelPlan plan = travelPlanMapper.selectById(collect.getCollectId());
            if (plan != null && plan.getDays() != null && plan.getDays() > 0) {
                daysList.add(plan.getDays());
            }
        }
        
        // 计算平均天数并分类
        if (!daysList.isEmpty()) {
            int avgDays = (int) Math.round(daysList.stream().mapToInt(Integer::intValue).average().orElse(0));
            if (avgDays > 0) {
                if (avgDays <= 2) {
                    return "1-2天";
                } else if (avgDays <= 5) {
                    return "3-5天";
                } else if (avgDays <= 7) {
                    return "5-7天";
                } else {
                    return "7天以上";
                }
            }
        }
        return "";
    }
        
    /**
     * 分析季节偏好（基于攻略和景点的bestSeason字段）
     */
    private String analyzeSeasonPreference(Long userId) {
        Map<String, Integer> seasonCount = new HashMap<>();
        seasonCount.put("春季", 0);
        seasonCount.put("夏季", 0);
        seasonCount.put("秋季", 0);
        seasonCount.put("冬季", 0);
        seasonCount.put("全年", 0);
        
        Set<String> keys = new HashSet<>();
        
        // 统计浏览的攻略
        QueryWrapper<UserBrowseHistory> browseWrapper = new QueryWrapper<>();
        browseWrapper.eq("user_id", userId);
        browseWrapper.eq("browse_type", 2); // 攻略
        List<UserBrowseHistory> planHistories = browseHistoryMapper.selectList(browseWrapper);
        
        for (UserBrowseHistory history : planHistories) {
            String key = "plan-" + history.getBrowseId();
            if (keys.contains(key)) continue;
            keys.add(key);
            
            TravelPlan plan = travelPlanMapper.selectById(history.getBrowseId());
            if (plan != null && plan.getBestSeason() != null) {
                countSeason(plan.getBestSeason(), seasonCount);
            }
        }
        
        // 统计收藏的攻略
        QueryWrapper<UserCollect> collectWrapper = new QueryWrapper<>();
        collectWrapper.eq("user_id", userId);
        collectWrapper.eq("collect_type", 1); // 攻略
        List<UserCollect> planCollects = userCollectMapper.selectList(collectWrapper);
        
        for (UserCollect collect : planCollects) {
            String key = "plan-" + collect.getCollectId();
            if (keys.contains(key)) continue;
            keys.add(key);
            
            TravelPlan plan = travelPlanMapper.selectById(collect.getCollectId());
            if (plan != null && plan.getBestSeason() != null) {
                countSeason(plan.getBestSeason(), seasonCount);
            }
        }
        
        // 统计浏览的景点
        QueryWrapper<UserBrowseHistory> attractionBrowseWrapper = new QueryWrapper<>();
        attractionBrowseWrapper.eq("user_id", userId);
        attractionBrowseWrapper.eq("browse_type", 1); // 景点
        List<UserBrowseHistory> attractionHistories = browseHistoryMapper.selectList(attractionBrowseWrapper);
        
        for (UserBrowseHistory history : attractionHistories) {
            String key = "attraction-" + history.getBrowseId();
            if (keys.contains(key)) continue;
            keys.add(key);
            
            Attraction attraction = attractionMapper.selectById(history.getBrowseId());
            if (attraction != null && attraction.getBestSeason() != null) {
                countSeason(attraction.getBestSeason(), seasonCount);
            }
        }
        
        // 统计收藏的景点
        QueryWrapper<UserCollect> attractionCollectWrapper = new QueryWrapper<>();
        attractionCollectWrapper.eq("user_id", userId);
        attractionCollectWrapper.eq("collect_type", 2); // 景点
        List<UserCollect> attractionCollects = userCollectMapper.selectList(attractionCollectWrapper);
        
        for (UserCollect collect : attractionCollects) {
            String key = "attraction-" + collect.getCollectId();
            if (keys.contains(key)) continue;
            keys.add(key);
            
            Attraction attraction = attractionMapper.selectById(collect.getCollectId());
            if (attraction != null && attraction.getBestSeason() != null) {
                countSeason(attraction.getBestSeason(), seasonCount);
            }
        }
        
        // 判断结果
        int totalSeasons = seasonCount.values().stream().mapToInt(Integer::intValue).sum();
        if (totalSeasons == 0) {
            return "";
        } else if (seasonCount.get("全年") == totalSeasons) {
            return "全年";
        } else {
            List<String> topSeasons = seasonCount.entrySet().stream()
                .filter(e -> e.getValue() > 0 && !e.getKey().equals("全年"))
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(2)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
            return topSeasons.isEmpty() ? "" : String.join("、", topSeasons);
        }
    }
    
    /**
     * 统计季节（辅助方法，减少代码重复）
     */
    private void countSeason(String seasonStr, Map<String, Integer> seasonCount) {
        if (seasonStr == null || seasonStr.trim().isEmpty()) {
            return;
        }
        String season = seasonStr.trim();
        
        if (season.contains("全年") || season.equals("全年")) {
            seasonCount.put("全年", seasonCount.get("全年") + 1);
        } else {
            if (season.contains("春")) {
                seasonCount.put("春季", seasonCount.get("春季") + 1);
            }
            if (season.contains("夏")) {
                seasonCount.put("夏季", seasonCount.get("夏季") + 1);
            }
            if (season.contains("秋")) {
                seasonCount.put("秋季", seasonCount.get("秋季") + 1);
            }
            if (season.contains("冬")) {
                seasonCount.put("冬季", seasonCount.get("冬季") + 1);
            }
        }
    }
    
    /**
     * 分析常去目的地（基于攻略的destination和景点的city字段）
     */
    private String analyzeFrequentDestinations(Long userId) {
        Map<String, Integer> cityCount = new HashMap<>();
        Set<String> keys = new HashSet<>();
        
        // 统计浏览的景点和攻略
        QueryWrapper<UserBrowseHistory> browseWrapper = new QueryWrapper<>();
        browseWrapper.eq("user_id", userId);
        List<UserBrowseHistory> allHistories = browseHistoryMapper.selectList(browseWrapper);
        
        for (UserBrowseHistory history : allHistories) {
            String key = history.getBrowseType() + "-" + history.getBrowseId();
            if (keys.contains(key)) continue;
            keys.add(key);
            
            String city = extractCity(history);
            if (city != null && !city.isEmpty()) {
                cityCount.put(city, cityCount.getOrDefault(city, 0) + 1);
            }
        }
        
        // 统计收藏的景点和攻略
        QueryWrapper<UserCollect> collectWrapper = new QueryWrapper<>();
        collectWrapper.eq("user_id", userId);
        List<UserCollect> allCollects = userCollectMapper.selectList(collectWrapper);
        
        for (UserCollect collect : allCollects) {
            String key = collect.getCollectType() + "-" + collect.getCollectId();
            if (keys.contains(key)) continue;
            keys.add(key);
            
            String city = extractCityFromCollect(collect);
            if (city != null && !city.isEmpty()) {
                cityCount.put(city, cityCount.getOrDefault(city, 0) + 1);
            }
        }
        
        // 返回前3个城市
        if (!cityCount.isEmpty()) {
            List<String> topCities = cityCount.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            
            return topCities.isEmpty() ? "" : String.join("、", topCities);
        }
        return "";
    }
    
    /**
     * 从浏览记录中提取城市名（辅助方法）
     */
    private String extractCity(UserBrowseHistory history) {
        if (history.getBrowseType() == 1) { // 景点
            Attraction attraction = attractionMapper.selectById(history.getBrowseId());
            if (attraction != null && attraction.getCity() != null) {
                return attraction.getCity().trim();
            }
        } else if (history.getBrowseType() == 2) { // 攻略
            TravelPlan plan = travelPlanMapper.selectById(history.getBrowseId());
            if (plan != null && plan.getDestination() != null) {
                return extractCityFromDestination(plan.getDestination());
            }
        }
        return null;
    }
    
    /**
     * 从收藏记录中提取城市名（辅助方法）
     */
    private String extractCityFromCollect(UserCollect collect) {
        if (collect.getCollectType() == 1) { // 攻略
            TravelPlan plan = travelPlanMapper.selectById(collect.getCollectId());
            if (plan != null && plan.getDestination() != null) {
                return extractCityFromDestination(plan.getDestination());
            }
        } else if (collect.getCollectType() == 2) { // 景点
            Attraction attraction = attractionMapper.selectById(collect.getCollectId());
            if (attraction != null && attraction.getCity() != null) {
                return attraction.getCity().trim();
            }
        }
        return null;
    }
    
    /**
     * 从目的地字符串中提取城市名（优化逻辑）
     */
    private String extractCityFromDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            return null;
        }
        String dest = destination.trim();
        
        // 如果包含"市"，提取到"市"为止
        if (dest.contains("市")) {
            int index = dest.indexOf("市");
            return dest.substring(0, index + 1);
        }
        // 如果包含"省"，提取到"省"为止（或整个字符串）
        if (dest.contains("省")) {
            int index = dest.indexOf("省");
            // 如果省后面还有内容，尝试提取城市
            if (index + 1 < dest.length()) {
                String afterProvince = dest.substring(index + 1).trim();
                if (afterProvince.contains("市")) {
                    int cityIndex = afterProvince.indexOf("市");
                    return afterProvince.substring(0, cityIndex + 1);
                }
            }
            return dest.substring(0, index + 1);
        }
        // 其他情况，直接返回原字符串
        return dest;
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 获取景点类型名称（与管理平台创建景点的类型对应）
     * 这些类型名称会用于：
     * 1. 景点推荐页面的筛选标签
     * 2. 用户画像的偏好统计
     * 3. 统计用户浏览相应类型景点的次数
     */
    private String getAttractionTypeName(Integer type) {
        switch (type) {
            case 1: return "自然风光";
            case 2: return "人文历史";
            case 3: return "主题公园";
            case 4: return "美食街";
            case 5: return "古镇古村";
            case 6: return "温泉度假";
            case 7: return "宗教场所";
            case 8: return "购物娱乐";
            default: return "其他";
        }
    }
    
    /**
     * 通过关键词匹配偏好类型（用于兼容旧数据或非标准类型名称）
     */
    private String matchPreferenceType(String typeName) {
        if (typeName == null || typeName.isEmpty()) {
            return null;
        }
        String lowerName = typeName.toLowerCase();
        
        // 自然风光
        if (lowerName.contains("自然") || lowerName.contains("景观") || lowerName.contains("风景") 
            || lowerName.contains("风光") || lowerName.contains("山水")) {
            return "自然风光";
        }
        // 人文历史
        if (lowerName.contains("历史") || lowerName.contains("人文") || lowerName.contains("文化") 
            || lowerName.contains("博物馆") || lowerName.contains("历史遗迹")) {
            return "人文历史";
        }
        // 主题公园
        if (lowerName.contains("主题") || lowerName.contains("公园") || lowerName.contains("乐园") 
            || lowerName.contains("游乐园")) {
            return "主题公园";
        }
        // 美食街
        if (lowerName.contains("美食") || lowerName.contains("食物") || lowerName.contains("餐厅") 
            || lowerName.contains("小吃")) {
            return "美食街";
        }
        // 古镇古村
        if (lowerName.contains("古镇") || lowerName.contains("古村") || lowerName.contains("村落") 
            || lowerName.contains("古城")) {
            return "古镇古村";
        }
        // 温泉度假
        if (lowerName.contains("温泉") || lowerName.contains("度假") || lowerName.contains("休闲") 
            || lowerName.contains("spa")) {
            return "温泉度假";
        }
        // 宗教场所
        if (lowerName.contains("宗教") || lowerName.contains("寺庙") || lowerName.contains("教堂") 
            || lowerName.contains("道观") || lowerName.contains("清真寺")) {
            return "宗教场所";
        }
        // 购物娱乐
        if (lowerName.contains("购物") || lowerName.contains("商业") || lowerName.contains("娱乐") 
            || lowerName.contains("商场") || lowerName.contains("夜市")) {
            return "购物娱乐";
        }
        
        return null;
    }
    
    /**
     * 将攻略标签映射到景点类型（统一统计口径）
     * 注意：返回的类型名称必须与景点类型名称一致
     */
    private String mapTagToPreference(String tag) {
        if (tag == null || tag.isEmpty()) {
            return null;
        }
        String lowerTag = tag.toLowerCase();
        
        // 自然风光
        if (lowerTag.contains("自然") || lowerTag.contains("风景") || lowerTag.contains("景观") 
            || lowerTag.contains("风光") || lowerTag.contains("山水")) {
            return "自然风光";
        }
        // 人文历史
        if (lowerTag.contains("历史") || lowerTag.contains("人文") || lowerTag.contains("文化") 
            || lowerTag.contains("博物馆") || lowerTag.contains("古迹") || lowerTag.contains("建筑")) {
            return "人文历史";
        }
        // 主题公园
        if (lowerTag.contains("主题") || lowerTag.contains("公园") || lowerTag.contains("乐园") 
            || lowerTag.contains("游乐园")) {
            return "主题公园";
        }
        // 美食街
        if (lowerTag.contains("美食") || lowerTag.contains("食物") || lowerTag.contains("吃") 
            || lowerTag.contains("餐厅") || lowerTag.contains("小吃")) {
            return "美食街";
        }
        // 古镇古村
        if (lowerTag.contains("古镇") || lowerTag.contains("古村") || lowerTag.contains("村落") 
            || lowerTag.contains("古城")) {
            return "古镇古村";
        }
        // 温泉度假
        if (lowerTag.contains("温泉") || lowerTag.contains("度假") || lowerTag.contains("休闲") 
            || lowerTag.contains("spa")) {
            return "温泉度假";
        }
        // 宗教场所
        if (lowerTag.contains("宗教") || lowerTag.contains("寺庙") || lowerTag.contains("教堂") 
            || lowerTag.contains("道观") || lowerTag.contains("清真寺")) {
            return "宗教场所";
        }
        // 购物娱乐
        if (lowerTag.contains("购物") || lowerTag.contains("商业") || lowerTag.contains("娱乐") 
            || lowerTag.contains("商场") || lowerTag.contains("夜市")) {
            return "购物娱乐";
        }
        
        return null;
    }
    
    /**
     * 将用户标签映射到标准兴趣标签
     */
    private String mapTagToStandardTag(String userTag) {
        if (userTag == null || userTag.isEmpty()) {
            return null;
        }
        String lowerTag = userTag.toLowerCase();
        
        // 按优先级匹配（更具体的标签优先）
        if (lowerTag.contains("夜景")) {
            return "夜景";
        }
        if (lowerTag.contains("摄影") || lowerTag.contains("拍照")) {
            return "摄影";
        }
        if (lowerTag.contains("美食") || lowerTag.contains("吃") || lowerTag.contains("餐厅")) {
            return "美食";
        }
        if (lowerTag.contains("情侣") || lowerTag.contains("恋爱") || lowerTag.contains("约会")) {
            return "情侣";
        }
        if (lowerTag.contains("亲子") || lowerTag.contains("家庭") || lowerTag.contains("带孩子")) {
            return "亲子";
        }
        if (lowerTag.contains("文化") || lowerTag.contains("人文")) {
            return "文化";
        }
        if (lowerTag.contains("自然") || lowerTag.contains("风景") || lowerTag.contains("景观")) {
            return "自然风光";
        }
        if (lowerTag.contains("历史") || lowerTag.contains("古迹")) {
            return "历史";
        }
        if (lowerTag.contains("建筑") || lowerTag.contains("古建筑")) {
            return "建筑";
        }
        if (lowerTag.contains("徒步") || lowerTag.contains("登山") || lowerTag.contains("爬山")) {
            return "徒步";
        }
        if (lowerTag.contains("温泉")) {
            return "温泉";
        }
        if (lowerTag.contains("博物馆")) {
            return "博物馆";
        }
        if (lowerTag.contains("夜生活") || lowerTag.contains("夜市")) {
            return "夜生活";
        }
        if (lowerTag.contains("购物") || lowerTag.contains("买")) {
            return "购物";
        }
        if (lowerTag.contains("度假") || lowerTag.contains("休闲")) {
            return "度假";
        }
        
        return null;
    }
    
    private String getPreferenceColor(String preference) {
        switch (preference) {
            case "自然风光":
                return "linear-gradient(135deg, #667eea 0%, #764ba2 100%)";
            case "人文历史":
                return "linear-gradient(135deg, #fa709a 0%, #fee140 100%)";
            case "主题公园":
                return "linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)";
            case "美食街":
                return "linear-gradient(135deg, #f093fb 0%, #f5576c 100%)";
            case "古镇古村":
                return "linear-gradient(135deg, #fad961 0%, #f76b1c 100%)";
            case "温泉度假":
                return "linear-gradient(135deg, #30cfd0 0%, #330867 100%)";
            case "宗教场所":
                return "linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)";
            case "购物娱乐":
                return "linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)";
            default:
                return "linear-gradient(135deg, #667eea 0%, #764ba2 100%)";
        }
    }
    
    private Map<String, Object> buildDefaultPreference() {
        Map<String, Object> result = new HashMap<>();
        result.put("primaryPreference", "自然风光");
        
        List<Map<String, Object>> distribution = new ArrayList<>();
        distribution.add(createPreferenceItem("自然风光", "🏞️", 25));
        distribution.add(createPreferenceItem("人文历史", "🏛️", 20));
        distribution.add(createPreferenceItem("主题公园", "🎢", 15));
        distribution.add(createPreferenceItem("美食街", "🍜", 15));
        distribution.add(createPreferenceItem("古镇古村", "🏘️", 10));
        distribution.add(createPreferenceItem("温泉度假", "♨️", 8));
        distribution.add(createPreferenceItem("宗教场所", "⛩️", 4));
        distribution.add(createPreferenceItem("购物娱乐", "🛍️", 3));
        
        result.put("distribution", distribution);
        return result;
    }
    
    private Map<String, Object> createPreferenceItem(String type, String icon, int percentage) {
        Map<String, Object> item = new HashMap<>();
        item.put("type", type);
        item.put("icon", icon);
        item.put("percentage", percentage);
        item.put("color", getPreferenceColor(type));
        return item;
    }
    
    private List<Map<String, Object>> buildDefaultInterestTags() {
        List<Map<String, Object>> tags = new ArrayList<>();
        tags.add(createTag("摄影", "📷", 10));
        tags.add(createTag("美食", "🍜", 9));
        tags.add(createTag("文化", "🎭", 8));
        tags.add(createTag("自然风光", "🏔️", 10));
        tags.add(createTag("历史", "📜", 7));
        tags.add(createTag("建筑", "🏛️", 6));
        tags.add(createTag("徒步", "🥾", 8));
        tags.add(createTag("温泉", "♨️", 5));
        tags.add(createTag("博物馆", "🏛️", 6));
        tags.add(createTag("夜生活", "🌙", 4));
        return tags;
    }
    
    private Map<String, Object> createTag(String name, String icon, int weight) {
        Map<String, Object> tag = new HashMap<>();
        tag.put("name", name);
        tag.put("icon", icon);
        tag.put("weight", weight);
        return tag;
    }
    
    /**
     * 构建空用户画像（所有字段为0或空，不使用默认值）
     */
    private Map<String, Object> buildEmptyPortrait() {
        Map<String, Object> result = new HashMap<>();
        result.put("browseCount", 0);
        result.put("favoriteCount", 0);
        result.put("searchCount", 0);
        result.put("avgStayTime", 0);
        result.put("primaryPreference", "");
        result.put("preferenceDistribution", new ArrayList<>());
        result.put("interestTags", new ArrayList<>());
        
        Map<String, Object> consumptionLevel = new HashMap<>();
        consumptionLevel.put("level", "");
        consumptionLevel.put("icon", "");
        consumptionLevel.put("color", "");
        result.put("consumptionLevel", consumptionLevel);
        result.put("avgConsumption", 0);
        result.put("consumptionFrequency", "");
        result.put("priceSensitivity", "");
        result.put("travelMode", "");
        result.put("tripDuration", "");
        result.put("seasonPreference", "");
        result.put("frequentDestinations", "");
        result.put("lastUpdateTime", LocalDateTime.now().toString().replace("T", " ").substring(0, 16));
        
        return result;
    }
    
    private Map<String, Object> buildDefaultPortrait() {
        Map<String, Object> result = new HashMap<>();
        result.put("browseCount", 141);
        result.put("favoriteCount", 11);
        result.put("searchCount", 68);
        result.put("avgStayTime", 15);
        result.putAll(buildDefaultPreference());
        result.put("interestTags", buildDefaultInterestTags());
        
        Map<String, Object> consumptionLevel = new HashMap<>();
        consumptionLevel.put("level", "品质舒适型");
        consumptionLevel.put("icon", "💳");
        consumptionLevel.put("color", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)");
        result.put("consumptionLevel", consumptionLevel);
        result.put("avgConsumption", 800);
        result.put("consumptionFrequency", "每月2-3次");
        result.put("priceSensitivity", "中等");
        result.put("travelMode", "自由行 + 自驾游");
        result.put("tripDuration", "3-5天");
        result.put("seasonPreference", "春季、秋季");
        result.put("frequentDestinations", "重庆、成都、西安");
        result.put("lastUpdateTime", LocalDateTime.now().toString().replace("T", " ").substring(0, 16));
        
        return result;
    }
}

