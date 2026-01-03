package com.zhly.controller;

import com.zhly.common.Result;
import com.zhly.service.AiService;
import com.zhly.config.AiConfig;
import com.zhly.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * AI控制器
 * 
 * @author zhly
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {
    
    @Autowired
    private AiService aiService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired(required = false)
    private AiConfig aiConfig;
    
    /**
     * 检查AI配置
     */
    @GetMapping("/check-config")
    public Result<Map<String, Object>> checkConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            
            if (aiConfig != null) {
                String apiKey = aiConfig.getApiKey();
                String modelName = aiConfig.getModelName();
                String apiUrl = aiConfig.getApiUrl();
                
                config.put("hasConfig", true);
                config.put("modelName", modelName);
                config.put("apiUrl", apiUrl);
                config.put("hasApiKey", apiKey != null && !apiKey.trim().isEmpty());
                config.put("apiKeyLength", apiKey != null ? apiKey.length() : 0);
                config.put("apiKeyPrefix", apiKey != null && apiKey.length() > 20 
                    ? apiKey.substring(0, Math.min(20, apiKey.length())) + "..." 
                    : "未配置");
                
                // 检查文心一言密钥格式
                if (modelName != null && (modelName.contains("文心") || modelName.contains("ernie"))) {
                    boolean isValidFormat = apiKey != null && apiKey.startsWith("bce-v3/") && apiKey.split("/").length >= 3;
                    config.put("isValidWenxinFormat", isValidFormat);
                    if (!isValidFormat && apiKey != null && !apiKey.isEmpty()) {
                        config.put("formatError", "文心一言密钥格式应为: bce-v3/{API_KEY}/{SECRET_KEY}");
                    }
                }
            } else {
                config.put("hasConfig", false);
                config.put("error", "AI配置未初始化");
            }
            
            return Result.success(config);
        } catch (Exception e) {
            return Result.error("检查AI配置失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成旅游攻略
     */
    @PostMapping("/generate-plan")
    public Result<Map<String, Object>> generateTravelPlan(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> params) {
        try {
            // 检查AI配置
            if (aiConfig == null) {
                return Result.error("AI配置未初始化，请联系管理员");
            }
            
            String apiKey = aiConfig.getApiKey();
            if (apiKey == null || apiKey.trim().isEmpty()) {
                return Result.error("AI API密钥未配置，请在系统配置中设置API密钥");
            }
            
            // 验证用户身份
            Long userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    userId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            // 如果没有用户ID，使用默认值
            if (userId == null) {
                userId = 1L;
            }
            
            // 获取请求参数
            String destination = (String) params.get("destination");
            Integer days = (Integer) params.get("days");
            String budget = (String) params.get("budget");
            String interests = (String) params.get("interests");
            String travelStyle = (String) params.get("travelStyle");
            Integer people = params.get("people") != null ? ((Number) params.get("people")).intValue() : null;
            String specialNeeds = (String) params.get("specialNeeds");
            
            // 构建完整的参数Map，传递给AI客户端服务
            Map<String, Object> aiParams = new HashMap<>();
            aiParams.put("destination", destination);
            aiParams.put("days", days);
            aiParams.put("budget", budget != null ? budget + "元" : "未指定");
            aiParams.put("interests", interests != null && !interests.isEmpty() ? interests : "无特殊偏好");
            
            // 构建旅游风格描述（包含人数和特殊需求）
            StringBuilder styleBuilder = new StringBuilder();
            if (people != null && people > 0) {
                styleBuilder.append(people).append("人出行，");
            }
            if (specialNeeds != null && !specialNeeds.trim().isEmpty()) {
                styleBuilder.append("特殊需求：").append(specialNeeds).append("，");
            }
            if (travelStyle != null && !travelStyle.trim().isEmpty()) {
                styleBuilder.append(travelStyle);
            } else {
                styleBuilder.append("休闲游");
            }
            aiParams.put("travelStyle", styleBuilder.toString());
            
            // 调用AI服务生成攻略（使用模板方法）
            String response = aiService.generateTravelPlan(userId, aiParams);
            
            // 构建返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("plan", response);
            result.put("destination", destination);
            result.put("days", days);
            result.put("budget", budget);
            result.put("interests", interests);
            result.put("travelStyle", travelStyle);
            result.put("people", people);
            result.put("specialNeeds", specialNeeds);
            
            return Result.success(result);
            
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            // 提供更友好的错误提示
            if (errorMessage != null) {
                if (errorMessage.contains("API密钥未配置") || errorMessage.contains("密钥格式错误")) {
                    return Result.error("AI API配置错误: " + errorMessage + "，请在管理端配置正确的API密钥");
                } else if (errorMessage.contains("网络") || errorMessage.contains("连接")) {
                    return Result.error("无法连接到AI服务，请检查网络连接");
                } else if (errorMessage.contains("超时")) {
                    return Result.error("AI服务响应超时，请稍后重试");
                }
            }
            return Result.error("生成旅游攻略失败: " + errorMessage);
        }
    }
    
    /**
     * 智能问答
     */
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestBody Map<String, Object> params) {
        try {
            // 验证用户身份
            Long userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    userId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            if (userId == null) {
                userId = 1L;
            }
            
            String question = (String) params.get("question");
            String modelName = "gpt-3.5-turbo";
            
            // 调用AI服务生成回答
            String response = aiService.generateContent(userId, question, modelName);
            
            Map<String, Object> result = new HashMap<>();
            result.put("answer", response);
            result.put("question", question);
            
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("AI问答失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取AI日志列表
     */
    @GetMapping("/logs")
    public Result<Map<String, Object>> getLogs(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        try {
            // 验证用户身份
            Long userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    userId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            if (userId == null) {
                userId = 1L;
            }
            
            Map<String, Object> result = aiService.getLogList(page, size, userId, status);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取AI日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取AI统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 验证用户身份
            Long userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    userId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            if (userId == null) {
                userId = 1L;
            }
            
            Map<String, Object> result = aiService.getUserAiStats(userId);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取AI统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有AI日志列表（管理员专用）
     */
    @GetMapping("/admin/logs")
    public Result<Map<String, Object>> getAllLogs(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status) {
        try {
            // 验证用户身份
            Long userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    userId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 管理员获取所有日志，不限制userId
            Map<String, Object> result = aiService.getLogList(page, size, null, status);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取AI日志失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取所有AI统计信息（管理员专用）
     */
    @GetMapping("/admin/statistics")
    public Result<Map<String, Object>> getAllStatistics(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 验证用户身份
            Long userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    userId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 获取所有AI统计
            Map<String, Object> result = aiService.getAiStatistics();
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取AI统计失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成AI内容（通用接口）
     */
    @PostMapping("/generate")
    public Result<String> generateContent(
            @RequestHeader(value = "Authorization", required = false) String token,
            @RequestParam Long userId,
            @RequestParam String request,
            @RequestParam(required = false, defaultValue = "gpt-3.5-turbo") String modelName) {
        try {
            // 验证用户身份
            Long currentUserId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    currentUserId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            if (currentUserId == null) {
                userId = 1L; // 使用默认管理员ID
            }
            
            // 调用AI服务生成内容
            String response = aiService.generateContent(userId, request, modelName);
            return Result.success(response);
            
        } catch (Exception e) {
            return Result.error("AI生成失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除AI日志
     */
    @DeleteMapping("/logs/{id}")
    public Result<String> deleteLog(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long id) {
        try {
            // 验证用户身份
            Long userId = null;
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                if (jwtUtil.validateToken(jwtToken)) {
                    userId = jwtUtil.getUserIdFromToken(jwtToken);
                }
            }
            
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            boolean success = aiService.deleteLog(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
            
        } catch (Exception e) {
            return Result.error("删除AI日志失败: " + e.getMessage());
        }
    }
}
