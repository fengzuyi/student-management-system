package com.student.management.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Grade {
    private Long id;
    private Student student;
    private Course course;
    private BigDecimal score;
    private String semester;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 