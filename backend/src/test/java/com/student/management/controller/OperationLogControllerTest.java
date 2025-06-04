package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.OperationLog;
import com.student.management.service.OperationLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OperationLogControllerTest {
    @InjectMocks
    private OperationLogController operationLogController;

    @Mock
    private OperationLogService operationLogService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试获取操作日志列表")
    void testList() {
        Map<String, Object> data = new HashMap<>();
        data.put("list", new ArrayList<>());
        data.put("total", 0);
        when(operationLogService.getList(anyString(), anyString(), anyString(), anyString(), anyInt(), anyInt())).thenReturn(data);
        Result<Map<String, Object>> result = operationLogController.list(null, null, null, null, 1, 10);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
    }

    @Test
    @DisplayName("测试删除操作日志")
    void testDelete() {
        doNothing().when(operationLogService).deleteLog(1L);
        Result<Void> result = operationLogController.delete(1L);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(operationLogService).deleteLog(1L);
    }

    @Test
    @DisplayName("测试批量删除操作日志")
    void testBatchDelete() {
        Map<String, List<Long>> request = new HashMap<>();
        request.put("ids", Arrays.asList(1L,2L));
        doNothing().when(operationLogService).batchDelete(anyList());
        Result<Void> result = operationLogController.batchDelete(request);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(operationLogService).batchDelete(Arrays.asList(1L,2L));
    }
} 