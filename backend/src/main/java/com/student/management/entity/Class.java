package com.student.management.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Class {
    private Long id;
    private String className;
    private String grade;
    private String major;
    private Integer studentCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    
    // 非数据库字段，用于关联查询
    private List<Student> students;
} 