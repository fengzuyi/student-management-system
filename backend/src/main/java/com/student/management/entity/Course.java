package com.student.management.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Course {
    private Long id;
    private String courseCode;
    private String courseName;
    private Double credits;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 