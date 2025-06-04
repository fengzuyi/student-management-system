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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class StudentControllerGetByIdTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试根据ID获取学生 - 正常场景")
    void testGetById() {
        // 准备测试数据
        Student mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setName("张三");
        mockStudent.setStudentNo("2024001");

        // 模拟service层返回
        when(studentService.getById(1L)).thenReturn(mockStudent);

        // 执行测试
        Result<Student> result = studentController.getById(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(mockStudent, result.getData());

        // 验证service方法被调用
        verify(studentService).getById(1L);
    }

    @Test
    @DisplayName("测试根据ID获取学生 - 不存在场景")
    void testGetByIdNotFound() {
        // 模拟service层返回null
        when(studentService.getById(anyLong())).thenReturn(null);

        // 执行测试
        Result<Student> result = studentController.getById(999L);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNull(result.getData());
    }
} 