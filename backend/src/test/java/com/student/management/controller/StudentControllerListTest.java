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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StudentControllerListTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试获取学生列表 - 正常场景")
    void testList() {
        // 准备测试数据
        List<Student> mockStudents = new ArrayList<>();
        Student student = new Student();
        student.setId(1L);
        student.setName("张三");
        student.setStudentNo("2024001");
        mockStudents.add(student);

        // 模拟service层返回
        when(studentService.getList(anyString(), anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(mockStudents);
        when(studentService.getCount(anyString(), anyString(), anyLong())).thenReturn(1);

        // 执行测试
        Result<Map<String, Object>> result = studentController.list("2024001", "张三", 1L, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().get("total"));
        assertEquals(mockStudents, result.getData().get("list"));

        // 验证service方法被调用
        verify(studentService).getList("2024001", "张三", 1L, 1, 10);
        verify(studentService).getCount("2024001", "张三", 1L);
    }

    @Test
    @DisplayName("测试获取学生列表 - 空结果场景")
    void testListEmpty() {
        // 模拟service层返回空列表
        when(studentService.getList(anyString(), anyString(), anyLong(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());
        when(studentService.getCount(anyString(), anyString(), anyLong())).thenReturn(0);

        // 执行测试
        Result<Map<String, Object>> result = studentController.list(null, null, null, 1, 10);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().get("total"));
        assertTrue(((List<?>) result.getData().get("list")).isEmpty());
    }

    @Test
    @DisplayName("测试获取学生列表 - 分页参数边界值")
    void testListPaginationBoundary() {
        // 测试页码为0的情况
        Result<Map<String, Object>> result = studentController.list(null, null, null, 0, 10);
        assertEquals(200, result.getCode());

        // 测试每页条数为0的情况
        result = studentController.list(null, null, null, 1, 0);
        assertEquals(200, result.getCode());

        // 测试每页条数过大的情况
        result = studentController.list(null, null, null, 1, 1000);
        assertEquals(200, result.getCode());
    }
} 