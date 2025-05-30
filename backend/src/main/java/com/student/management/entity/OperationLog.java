package com.student.management.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLog {
    private Long id;
    private Long userId;
    private String operationType;
    private String operationContent;
    private LocalDateTime createTime;
    
    // 关联用户信息（非数据库字段）
    private String username;
    private String realName;
} 