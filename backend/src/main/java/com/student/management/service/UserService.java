package com.student.management.service;

import com.student.management.common.Result;
import com.student.management.dto.UserDTO;
import java.util.Map;

/**
 * 用户管理服务接口
 */
public interface UserService {
    /**
     * 获取用户列表
     * @param username 用户名（可选）
     * @param realName 真实姓名（可选）
     * @param role 角色（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 用户列表和总数
     */
    Result<Map<String, Object>> listUsers(String username, String realName, String role, Integer pageNum, Integer pageSize);

    /**
     * 创建用户
     * @param userDTO 用户信息
     * @return 创建结果
     */
    Result createUser(UserDTO userDTO);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 用户信息
     * @return 更新结果
     */
    Result updateUser(Long id, UserDTO userDTO);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    Result deleteUser(Long id);

    /**
     * 获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    Result getUserById(Long id);
} 