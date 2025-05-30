package com.student.management.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
public class Grade {
    private Long id;
    private Long studentId;
    private Long courseId;
    private BigDecimal score;
    private String semester;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
} 