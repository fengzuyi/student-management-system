package com.student.management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class GradeDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNo;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private BigDecimal score;
    private String semester;
} 