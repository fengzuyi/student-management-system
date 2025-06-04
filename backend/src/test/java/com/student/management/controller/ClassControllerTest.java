package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.Class;
import com.student.management.service.ClassService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClassControllerTest {
    @InjectMocks
    private ClassController classController;

    @Mock
    private ClassService classService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试获取班级列表")
    void testList() {
        List<Class> mockList = new ArrayList<>();
        Class clazz = new Class();
        clazz.setId(1L);
        clazz.setClassName("一班");
        mockList.add(clazz);
        when(classService.getList(anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(mockList);
        when(classService.getCount(anyString(), anyString(), anyString())).thenReturn(1);
        Result<Map<String, Object>> result = classController.list("一班", "2024", "计算机", 1, 10);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().get("total"));
        assertEquals(mockList, result.getData().get("list"));
    }

    @Test
    @DisplayName("测试根据ID获取班级")
    void testGetById() {
        Class clazz = new Class();
        clazz.setId(1L);
        clazz.setClassName("一班");
        when(classService.getById(1L)).thenReturn(clazz);
        Result<Class> result = classController.getById(1L);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(clazz, result.getData());
    }

    @Test
    @DisplayName("测试新增班级")
    void testAdd() {
        Class clazz = new Class();
        clazz.setClassName("一班");
        Result<Void> result = classController.add(clazz);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(classService).add(clazz);
    }

    @Test
    @DisplayName("测试更新班级")
    void testUpdate() {
        Class clazz = new Class();
        clazz.setId(1L);
        clazz.setClassName("一班");
        Result<Void> result = classController.update(clazz);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(classService).update(clazz);
    }

    @Test
    @DisplayName("测试删除班级")
    void testDelete() {
        Result<Void> result = classController.delete("1,2");
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(classService).batchDelete(anyList());
    }

    @Test
    @DisplayName("测试导出班级Excel")
    void testExportExcel() {
        when(classService.exportExcel()).thenReturn(new byte[]{1,2,3});
        ResponseEntity<byte[]> response = classController.exportExcel();
        assertNotNull(response);
        assertArrayEquals(new byte[]{1,2,3}, response.getBody());
    }
} 