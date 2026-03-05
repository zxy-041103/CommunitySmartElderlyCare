package com.community.elderly.config;

import com.community.elderly.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("JWT认证过滤器执行，请求路径: {}", requestURI);
        
        try {
            String token = getTokenFromRequest(request);
            log.info("JWT认证过滤器执行，token: {}", token);
            if (token != null && !jwtUtil.isTokenExpired(token)) {
                Claims claims = jwtUtil.getClaimsFromToken(token);
                Long userId = claims.get("userId", Long.class);
                String roleType = claims.get("roleType", String.class);
                log.info("JWT认证过滤器解析token，userId: {}, roleType: {}", userId, roleType);

                // 创建权限列表
                List<GrantedAuthority> authorities = new ArrayList<>();
                if (roleType != null) {
                    // 添加角色权限，格式为 ROLE_+角色名
                    String authority = "ROLE_" + roleType.toUpperCase();
                    authorities.add(new SimpleGrantedAuthority(authority));
                    log.info("JWT认证过滤器添加权限: {}", authority);
                }

                // 创建认证对象，使用userId作为principal，添加权限信息
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userId.toString(), null, authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 设置认证信息到上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("JWT认证过滤器设置认证信息成功，请求路径: {}", requestURI);
            } else {
                log.info("JWT认证过滤器：token为null或已过期，请求路径: {}", requestURI);
            }
        } catch (Exception e) {
            log.error("JWT 认证失败，请求路径: {}，错误: {}", requestURI, e.getMessage());
        }

        chain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
