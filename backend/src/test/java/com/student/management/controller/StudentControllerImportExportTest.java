package com.student.management.controller;

import com.student.management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StudentControllerImportExportTest {

    @InjectMocks
    private StudentController studentController;

    @Mock
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试导入Excel - 正常场景")
    void testImportExcel() {
        // 准备测试数据
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.xlsx",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "test data".getBytes()
        );

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("success", true);
        mockResult.put("message", "导入成功");

        // 模拟service层返回
        when(studentService.importExcel(any())).thenReturn(mockResult);

        // 执行测试
        ResponseEntity<?> response = studentController.importExcel(file);

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResult, response.getBody());

        // 验证service方法被调用
        verify(studentService).importExcel(any());
    }

    @Test
    @DisplayName("测试导入Excel - 空文件")
    void testImportExcelEmptyFile() {
        // 准备测试数据 - 空文件
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.xlsx",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            new byte[0]
        );

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("success", false);
        mockResult.put("message", "文件为空");

        // 模拟service层返回
        when(studentService.importExcel(any())).thenReturn(mockResult);

        // 执行测试
        ResponseEntity<?> response = studentController.importExcel(file);

        // 验证结果
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(mockResult, response.getBody());
    }

    @Test
    @DisplayName("测试导入Excel - 不支持的文件格式")
    void testImportExcelInvalidFormat() {
        // 准备测试数据 - 不支持的文件格式
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "test data".getBytes()
        );

        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("success", false);
        mockResult.put("message", "不支持的文件格式");

        // 模拟service层返回
        when(studentService.importExcel(any())).thenReturn(mockResult);

        // 执行测试
        ResponseEntity<?> response = studentController.importExcel(file);

        // 验证结果
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        assertEquals(mockResult, response.getBody());
    }

    @Test
    @DisplayName("测试导出Excel - 正常场景")
    void testExportExcel() {
        // 模拟service层返回
        byte[] mockExcelData = "mock excel data".getBytes();
        when(studentService.exportExcel()).thenReturn(mockExcelData);

        // 执行测试
        ResponseEntity<byte[]> response = studentController.exportExcel();

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockExcelData, response.getBody());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("filename"));

        // 验证service方法被调用
        verify(studentService).exportExcel();
    }

    @Test
    @DisplayName("测试下载模板 - 正常场景")
    void testDownloadTemplate() {
        // 模拟service层返回
        byte[] mockTemplateData = "mock template data".getBytes();
        when(studentService.generateTemplate()).thenReturn(mockTemplateData);

        // 执行测试
        ResponseEntity<byte[]> response = studentController.downloadTemplate();

        // 验证结果
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockTemplateData, response.getBody());
        assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        assertTrue(response.getHeaders().getContentDisposition().toString().contains("filename"));

        // 验证service方法被调用
        verify(studentService).generateTemplate();
    }
} 