package com.student.management.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OperationLogDTO {
    private Long id;
    private Long userId;
    private String operationType;
    private String operationContent;
    private LocalDateTime createTime;
    private String username;
    private String realName;
} 