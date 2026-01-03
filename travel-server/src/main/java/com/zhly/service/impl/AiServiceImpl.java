package com.zhly.service.impl;

import com.zhly.entity.AiGenerateLog;
import com.zhly.mapper.AiGenerateLogMapper;
import com.zhly.service.AiService;
import com.zhly.service.AiClientService;
import com.zhly.config.AiConfig;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI服务实现类
 * 
 * @author zhly
 * @since 2024-01-01
 */
@Service
public class AiServiceImpl extends ServiceImpl<AiGenerateLogMapper, AiGenerateLog> implements AiService {
    
    @Autowired
    private AiGenerateLogMapper aiGenerateLogMapper;
    
    @Autowired
    private AiClientService aiClientService;
    
    @Autowired(required = false)
    private AiConfig aiConfig;
    
    @Override
    public Map<String, Object> getLogList(Integer page, Integer size, Long userId, Integer status) {
        try {
            // 计算偏移量
            int offset = (page - 1) * size;
            List<AiGenerateLog> logs = aiGenerateLogMapper.selectLogList(page, size, offset, userId, status);
            Long total = aiGenerateLogMapper.selectLogCount(userId, status);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", logs);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("获取AI日志失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getAiStatistics() {
        try {
            return aiGenerateLogMapper.selectAiStatistics();
        } catch (Exception e) {
            throw new RuntimeException("获取AI统计失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getUserAiStats(Long userId) {
        try {
            return aiGenerateLogMapper.selectUserAiStats(userId);
        } catch (Exception e) {
            throw new RuntimeException("获取用户AI统计失败: " + e.getMessage());
        }
    }
    
    @Override
    public String generateContent(Long userId, String request, String modelName) {
        long startTime = System.currentTimeMillis();
        String response = "";
        Integer status = 0;
        Integer tokensUsed = 0;
        Double cost = 0.0;
        
        try {
            // 调用真实的AI客户端生成内容
            response = aiClientService.generateContent(request, modelName, 1000, 0.7);
            
            // 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 模拟token使用量和成本计算
            tokensUsed = response.length() / 4; // 粗略估算
            cost = tokensUsed * 0.0001; // 模拟成本
            
            status = 1; // 成功
            
            // 记录生成日志
            logAiGenerate(userId, request, response, status, (int) responseTime, modelName, tokensUsed, cost);
            
            return response;
            
        } catch (Exception e) {
            // 记录失败日志
            long responseTime = System.currentTimeMillis() - startTime;
            response = "AI生成失败: " + e.getMessage();
            logAiGenerate(userId, request, response, status, (int) responseTime, modelName, tokensUsed, cost);
            throw new RuntimeException("AI生成失败: " + e.getMessage());
        }
    }
    
    @Override
    public String generateTravelPlan(Long userId, Map<String, Object> params) {
        long startTime = System.currentTimeMillis();
        String response = "";
        Integer status = 0;
        Integer tokensUsed = 0;
        Double cost = 0.0;
        String modelName = aiConfig != null ? aiConfig.getModelName() : "gpt-3.5-turbo";
        
        try {
            // 构建请求描述（用于日志记录）
            String request = String.format("生成%s %d天旅游攻略，预算：%s，兴趣：%s，风格：%s",
                params.get("destination"),
                params.get("days"),
                params.get("budget"),
                params.get("interests"),
                params.get("travelStyle")
            );
            
            // 调用AI客户端服务生成攻略（使用模板方法）
            response = aiClientService.generateTravelPlan(params);
            
            // 检查响应是否包含错误信息
            if (response != null && (
                response.contains("AI服务暂时不可用") || 
                response.contains("AI服务调用失败") ||
                response.contains("API密钥未配置") ||
                response.contains("密钥格式错误")
            )) {
                throw new RuntimeException(response);
            }
            
            // 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            
            // 模拟token使用量和成本计算
            tokensUsed = response.length() / 4; // 粗略估算
            cost = tokensUsed * 0.0001; // 模拟成本
            
            status = 1; // 成功
            
            // 记录生成日志
            logAiGenerate(userId, request, response, status, (int) responseTime, modelName, tokensUsed, cost);
            
            return response;
            
        } catch (Exception e) {
            // 记录失败日志
            long responseTime = System.currentTimeMillis() - startTime;
            String request = String.format("生成%s旅游攻略", params.get("destination"));
            response = "AI生成失败: " + e.getMessage();
            logAiGenerate(userId, request, response, status, (int) responseTime, modelName, tokensUsed, cost);
            throw new RuntimeException("AI生成攻略失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean logAiGenerate(Long userId, String request, String response, Integer status, Integer responseTime, String modelName, Integer tokensUsed, Double cost) {
        try {
            AiGenerateLog log = new AiGenerateLog();
            log.setUserId(userId);
            log.setRequestContent(request);
            log.setResponseContent(response);
            log.setStatus(status);
            log.setCostTokens(tokensUsed);
            log.setResponseTime(responseTime);  // 保存响应时间
            log.setCreateTime(LocalDateTime.now());
            
            return aiGenerateLogMapper.insert(log) > 0;
        } catch (Exception e) {
            throw new RuntimeException("记录AI生成日志失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean cleanExpiredLogs(Integer days) {
        try {
            return aiGenerateLogMapper.cleanExpiredLogs(days) > 0;
        } catch (Exception e) {
            throw new RuntimeException("清理过期日志失败: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteLog(Long id) {
        try {
            return aiGenerateLogMapper.deleteById(id) > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除AI日志失败: " + e.getMessage());
        }
    }
}