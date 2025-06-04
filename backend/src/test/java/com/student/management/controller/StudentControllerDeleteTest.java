package com.student.management.controller;

import com.student.management.common.Result;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class StudentControllerDeleteTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试删除学生 - 正常场景")
    void testDelete() {
        // 准备测试数据
        List<Long> ids = List.of(1L, 2L);
        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", ids);

        // 执行测试
        Result<Void> result = studentController.delete(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证service方法被调用
        verify(studentService).delete(ids);
    }

    @Test
    @DisplayName("测试删除学生 - 空ID列表")
    void testDeleteEmptyIds() {
        // 准备测试数据 - 空ID列表
        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", new ArrayList<>());

        // 执行测试
        Result<Void> result = studentController.delete(request);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证service方法被调用
        verify(studentService).delete(anyList());
    }
} 