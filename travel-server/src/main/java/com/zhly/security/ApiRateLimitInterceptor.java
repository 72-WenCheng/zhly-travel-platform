package com.zhly.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;

/**
 * API限流拦截器
 */
@Component
public class ApiRateLimitInterceptor implements HandlerInterceptor {

    private final SecurityPolicyService securityPolicyService;
    private final RateLimitService rateLimitService;

    public ApiRateLimitInterceptor(SecurityPolicyService securityPolicyService,
                                   RateLimitService rateLimitService) {
        this.securityPolicyService = securityPolicyService;
        this.rateLimitService = rateLimitService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SecurityPolicy policy = securityPolicyService.getPolicy();
        if (!policy.isApiRateLimitEnabled()) {
            return true;
        }
        
        // 排除统计类接口、上传接口和管理端接口，这些接口不应该被严格限流
        String requestUri = request.getRequestURI();
        if (requestUri != null) {
            // 排除常用操作统计接口
            if (requestUri.contains("/usage/common-actions")) {
                return true;
            }
            // 排除其他统计类接口
            if (requestUri.contains("/stats") || requestUri.contains("/statistics")) {
                return true;
            }
            // 排除上传接口，上传接口已经有自己的频率限制注解
            if (requestUri.contains("/upload/")) {
                return true;
            }
            // 排除管理端接口，管理端是内部操作，不应该被严格限流
            if (requestUri.contains("/api/admin/")) {
                return true;
            }
            // 排除攻略管理接口（管理端操作）
            if (requestUri.contains("/travel-plan/") && (requestUri.contains("/audit") || requestUri.contains("/delete"))) {
                return true;
            }
        }
        
        // 仅对可能造成压力的写操作做限流，避免普通查询受影响
        String method = request.getMethod();
        if (!"POST".equalsIgnoreCase(method)
                && !"PUT".equalsIgnoreCase(method)
                && !"PATCH".equalsIgnoreCase(method)
                && !"DELETE".equalsIgnoreCase(method)) {
            return true;
        }
        String clientIp = resolveClientIp(request);
        String key = "security:rate:" + clientIp;
        boolean allowed = rateLimitService.tryAcquire(key, policy.getApiRateLimitPerMinute(), Duration.ofMinutes(1));
        if (!allowed) {
            throw new RuntimeException("请求过于频繁，请稍后再试");
        }
        return true;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            return ip.split(",")[0].trim();
        }
        return ip != null ? ip : "unknown";
    }
}

