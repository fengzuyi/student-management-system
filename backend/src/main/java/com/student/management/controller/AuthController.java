package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.dto.ChangePasswordDTO;
import com.student.management.dto.LoginDTO;
import com.student.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        return authService.login(loginDTO);
    }

    @GetMapping("/info")
    public Result getUserInfo() {
        return authService.getUserInfo();
    }

    @PostMapping("/logout")
    public Result logout() {
        return authService.logout();
    }

    @PostMapping("/change-password")
    public Result changePassword(@RequestBody @Valid ChangePasswordDTO changePasswordDTO) {
        return authService.changePassword(changePasswordDTO);
    }
} 