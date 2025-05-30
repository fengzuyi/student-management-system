package com.student.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @Size(min = 6, message = "密码长度不能小于6位")
    private String password;

    @NotBlank(message = "真实姓名不能为空")
    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String realName;

    @NotBlank(message = "角色不能为空")
    @Pattern(regexp = "^(ADMIN|TEACHER)$", message = "角色只能是ADMIN或TEACHER")
    private String role;

    private LocalDateTime createTime;
} 