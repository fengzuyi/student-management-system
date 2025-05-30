package com.student.management.service.impl;

import com.student.management.common.Result;
import com.student.management.dto.ChangePasswordDTO;
import com.student.management.dto.LoginDTO;
import com.student.management.entity.User;
import com.student.management.mapper.UserMapper;
import com.student.management.service.AuthService;
import com.student.management.util.JwtUtil;
import com.student.management.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public Result login(LoginDTO loginDTO) {
        User user = userMapper.findByUsername(loginDTO.getUsername());
        if (user == null) {
            return Result.error("用户名或密码错误");
        }

        // 直接比较明文密码
        if (!loginDTO.getPassword().equals(user.getPassword())) {
            return Result.error("用户名或密码错误");
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId());

        // 返回用户信息（不包含密码）
        user.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", user);

        return Result.success(data);
    }

    @Override
    public Result getUserInfo() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return Result.error("未登录");
        }

        User user = userMapper.findById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setPassword(null);
        return Result.success(user);
    }

    @Override
    public Result logout() {
        // 由于使用JWT，服务端不需要维护会话状态，直接返回成功即可
        return Result.success(null);
    }

    @Override
    @Transactional
    public Result changePassword(ChangePasswordDTO changePasswordDTO) {
        logger.debug("开始修改密码");
        
        // 获取当前登录用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        logger.debug("当前用户ID: {}", userId);
        
        if (userId == null) {
            logger.error("用户未登录");
            return Result.error("用户未登录");
        }

        // 获取用户信息
        User currentUser = userMapper.findById(userId);
        logger.debug("获取到用户信息: {}", currentUser != null ? currentUser.getUsername() : "null");
        
        if (currentUser == null) {
            logger.error("用户不存在");
            return Result.error("用户不存在");
        }

        // 验证原密码
        logger.debug("验证原密码");
        if (!changePasswordDTO.getOldPassword().equals(currentUser.getPassword())) {
            logger.error("原密码错误");
            return Result.error("原密码错误");
        }

        // 更新密码（直接使用新密码，不加密）
        logger.debug("开始更新密码");
        int updated = userMapper.updateById(userId, changePasswordDTO.getNewPassword());
        logger.debug("密码更新结果: {}", updated > 0 ? "成功" : "失败");
        
        if (updated > 0) {
            logger.info("密码修改成功");
            return Result.success("密码修改成功");
        } else {
            logger.error("密码修改失败");
            return Result.error("密码修改失败");
        }
    }
} 