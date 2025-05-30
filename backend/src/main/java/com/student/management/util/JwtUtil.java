package com.student.management.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.management.entity.User;
import com.student.management.mapper.UserMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.secret:your-256-bit-secret}")
    private String secret;

    @Autowired
    private UserMapper userMapper;

    private SecretKey key;

    @PostConstruct
    public void init() {
        try {
            // 使用固定的密钥
            byte[] keyBytes = secret.getBytes();
            this.key = Keys.hmacShaKeyFor(keyBytes);
            logger.info("JWT key initialized successfully with HS384 algorithm");
        } catch (Exception e) {
            logger.error("Failed to initialize JWT key: {}", e.getMessage());
            throw new IllegalStateException("Failed to initialize JWT key", e);
        }
    }

    private Key getSigningKey() {
        return key;
    }

    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS384)
                .compact();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Long userId = getUserIdFromToken(token);
        // 从数据库获取用户信息
        User user = userMapper.findById(userId);
        if (user == null) {
            logger.error("User not found for ID: {}", userId);
            return null;
        }
        // 清除密码等敏感信息
        user.setPassword(null);
        // 创建包含完整用户信息和角色的认证对象
        return new UsernamePasswordAuthenticationToken(
            user,
            null,
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
        );
    }
} 