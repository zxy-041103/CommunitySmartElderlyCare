package com.eldercare.interceptor;

import com.eldercare.utils.JwtUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || !jwtUtils.validateToken(token)) {
            response.setStatus(401);
            response.setContentType("application/json;charset=utf-8");
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("msg", "未登录或登录已过期");
            response.getWriter().write(new ObjectMapper().writeValueAsString(result));
            return false;
        }
        request.setAttribute("userId", jwtUtils.getUserId(token));
        request.setAttribute("username", jwtUtils.getUsername(token));
        request.setAttribute("role", jwtUtils.getRole(token));
        return true;
    }
}
