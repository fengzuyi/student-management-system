package com.student.management.service;

import com.student.management.common.Result;
import com.student.management.dto.ChangePasswordDTO;
import com.student.management.dto.LoginDTO;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 用户登录
     * @param loginDTO 登录信息（用户名和密码）
     * @return 登录结果，包含token和用户信息
     */
    Result login(LoginDTO loginDTO);

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    Result getUserInfo();

    /**
     * 用户登出
     * @return 登出结果
     */
    Result logout();

    /**
     * 修改密码
     * @param changePasswordDTO 密码修改信息（旧密码和新密码）
     * @return 修改结果
     */
    Result changePassword(ChangePasswordDTO changePasswordDTO);
} 