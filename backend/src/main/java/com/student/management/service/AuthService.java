package com.student.management.service;

import com.student.management.common.Result;
import com.student.management.dto.ChangePasswordDTO;
import com.student.management.dto.LoginDTO;

public interface AuthService {
    Result login(LoginDTO loginDTO);
    Result getUserInfo();
    Result logout();
    Result changePassword(ChangePasswordDTO changePasswordDTO);
} 