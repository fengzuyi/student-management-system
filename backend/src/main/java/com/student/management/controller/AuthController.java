package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.dto.ChangePasswordDTO;
import com.student.management.dto.LoginDTO;
import com.student.management.entity.OperationLog;
import com.student.management.entity.User;
import com.student.management.service.AuthService;
import com.student.management.service.OperationLogService;
import com.student.management.service.UserService;
import com.student.management.util.JwtUtil;
import com.student.management.util.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserService userService;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public Result login(@RequestBody @Valid LoginDTO loginDTO) {
        Result result = authService.login(loginDTO);
        
        // 如果登录成功，记录登录日志
        if (result.getCode() == 200) {
            Map<String, Object> data = (Map<String, Object>) result.getData();
            User user = (User) data.get("userInfo");
            OperationLog log = new OperationLog();
            log.setUserId(user.getId());
            log.setOperationType("用户登录");
            log.setOperationContent("用户 " + user.getUsername() + " 登录系统");
            log.setCreateTime(LocalDateTime.now());
            operationLogService.addLog(log);
        }
        
        return result;
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        return authService.getUserInfo();
    }

    @PostMapping("/logout")
    public Result logout() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId != null) {
            Result userResult = userService.getUserById(userId);
            if (userResult.getCode() == 200) {
                User user = (User) userResult.getData();
                // 记录登出日志
                OperationLog log = new OperationLog();
                log.setUserId(userId);
                log.setOperationType("用户登出");
                log.setOperationContent("用户 " + user.getUsername() + " 退出系统");
                log.setCreateTime(LocalDateTime.now());
                operationLogService.addLog(log);
            }
        }
        return authService.logout();
    }

    @PostMapping("/change-password")
    public Result changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        return authService.changePassword(changePasswordDTO);
    }
} 