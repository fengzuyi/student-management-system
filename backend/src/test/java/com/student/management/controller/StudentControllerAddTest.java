package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.Student;
import com.student.management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentControllerAddTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试添加学生 - 正常场景")
    void testAdd() {
        // 准备测试数据
        Student student = new Student();
        student.setName("张三");
        student.setStudentNo("2024001");
        student.setGender("男");
        student.setPhone("13800138000");
        student.setEmail("zhangsan@example.com");

        // 执行测试
        Result<Void> result = studentController.add(student);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证service方法被调用
        verify(studentService).add(student);
    }

    @Test
    @DisplayName("测试添加学生 - 必填字段缺失")
    void testAddMissingRequiredFields() {
        // 准备测试数据 - 缺少必填字段
        Student student = new Student();
        student.setName("张三");
        // 缺少studentNo

        // 执行测试
        Result<Void> result = studentController.add(student);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证service方法被调用
        verify(studentService).add(student);
    }
} 