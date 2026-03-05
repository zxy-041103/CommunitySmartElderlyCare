package com.community.elderly.config;

import com.community.elderly.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final int SC_TOO_MANY_REQUESTS = 429;
    private RedisTemplate<String, Object> redisTemplate;

    // 无参构造函数，用于Spring Boot自动注入
    public RateLimitInterceptor() {
    }

    // 有参构造函数，用于手动注入
    public RateLimitInterceptor(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Setter方法，用于Spring Boot自动注入
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 限流配置
    private static final int MAX_REQUESTS_PER_MINUTE = 60; // 每分钟最大请求数
    private static final String RATE_LIMIT_PREFIX = "rate:limit:";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取客户端IP地址
        String clientIp = getClientIp(request);
        // 获取请求路径
        String requestPath = request.getRequestURI();
        // 构建限流键
        String rateLimitKey = RATE_LIMIT_PREFIX + clientIp + ":" + requestPath;

        // 检查并更新限流计数
        if (!checkRateLimit(rateLimitKey)) {
            // 超过限流阈值，返回429状态码
            response.setStatus(SC_TOO_MANY_REQUESTS);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(new ObjectMapper().writeValueAsString(Result.error("请求过于频繁，请稍后再试")));
            writer.flush();
            writer.close();
            return false;
        }

        return true;
    }

    /**
     * 检查并更新限流计数
     */
    private boolean checkRateLimit(String key) {
        try {
            // 获取当前计数
            Long currentCount = redisTemplate.opsForValue().increment(key);
            if (currentCount == 1) {
                // 第一次请求，设置过期时间为1分钟
                redisTemplate.expire(key, 1, TimeUnit.MINUTES);
            }

            // 检查是否超过限流阈值
            return currentCount <= MAX_REQUESTS_PER_MINUTE;
        } catch (Exception e) {
            // Redis异常时，默认放行，避免影响正常业务
            return true;
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
