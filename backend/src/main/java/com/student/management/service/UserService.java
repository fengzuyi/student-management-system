package com.student.management.service;

import com.student.management.common.Result;
import com.student.management.dto.UserDTO;
import java.util.Map;

public interface UserService {
    Result<Map<String, Object>> listUsers(String username, String realName, String role, Integer pageNum, Integer pageSize);
    Result createUser(UserDTO userDTO);
    Result updateUser(Long id, UserDTO userDTO);
    Result deleteUser(Long id);
    Result getUserById(Long id);
} 