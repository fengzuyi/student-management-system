package com.student.management.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.management.common.Result;
import com.student.management.entity.User;
import com.student.management.mapper.UserMapper;
import com.student.management.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenInterceptor extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        logger.debug("处理请求: {} {}", method, requestURI);
        
        // 允许OPTIONS请求通过
        if ("OPTIONS".equals(method)) {
            logger.debug("允许OPTIONS请求通过");
            filterChain.doFilter(request, response);
            return;
        }

        // 只允许登录和登出接口免验证
        if (requestURI.equals("/api/auth/login") || 
            requestURI.equals("/api/auth/logout")) {
            logger.debug("允许访问免验证接口: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        // 获取token
        String token = request.getHeader("Authorization");
        logger.debug("请求头中的Authorization: {}", token);
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                // 验证token
                if (jwtUtil.validateToken(token)) {
                    // 获取token中的用户ID
                    Long userId = jwtUtil.getUserIdFromToken(token);
                    // 检查token是否是用户最新的token
                    User user = userMapper.findById(userId);
                    if (user != null && token.equals(user.getLastToken())) {
                        logger.debug("Token验证成功");
                        // 设置认证信息
                        Authentication authentication = jwtUtil.getAuthentication(token);
                        if (authentication != null) {
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            logger.debug("已设置认证信息: {}", authentication);
                            filterChain.doFilter(request, response);
                            return;
                        } else {
                            logger.error("获取认证信息失败");
                        }
                    } else {
                        logger.error("Token已失效（已被其他设备登录）");
                        response.setContentType("application/json;charset=UTF-8");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write(objectMapper.writeValueAsString(Result.error("您的账号已在其他设备登录，请重新登录")));
                        return;
                    }
                } else {
                    logger.error("Token验证失败");
                }
            } catch (Exception e) {
                logger.error("Token处理异常", e);
            }
        } else {
            logger.error("未找到有效的Token");
        }

        // token无效或不存在，返回401
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String errorMessage = "未登录或登录已过期";
        logger.error("返回401错误: {}", errorMessage);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(errorMessage)));
    }
} 