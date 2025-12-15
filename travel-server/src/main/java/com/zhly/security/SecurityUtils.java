package com.zhly.security;

import com.zhly.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 基于 JWT 的简单鉴权工具
 */
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final JwtUtil jwtUtil;

    /**
     * 从 Authorization Bearer Token 中解析当前登录用户ID
     */
    public Long getLoginUserId(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String authHeader = request.getHeader("Authorization");
        String token = extractBearerToken(authHeader);
        if (!StringUtils.hasText(token)) {
            return null;
        }

        try {
            if (!jwtUtil.validateToken(token)) {
                return null;
            }
            return jwtUtil.getUserIdFromToken(token);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String extractBearerToken(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            return null;
        }
        // 兼容大小写与额外空格
        if (authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7).trim();
        }
        return authHeader.trim();
    }
}
