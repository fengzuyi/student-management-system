package com.student.management.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Student {
    private Long id;
    private String studentNo;
    private String name;
    private Long classId;
    private String gender;
    private String phone;
    private String email;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 非数据库字段，用于关联查询
    private Class classInfo;
} 