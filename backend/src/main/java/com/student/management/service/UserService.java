package com.student.management.service;

import com.student.management.common.Result;
import com.student.management.dto.UserDTO;

public interface UserService {
    Result listUsers();
    Result createUser(UserDTO userDTO);
    Result updateUser(Long id, UserDTO userDTO);
    Result deleteUser(Long id);
    Result getUserById(Long id);
} 