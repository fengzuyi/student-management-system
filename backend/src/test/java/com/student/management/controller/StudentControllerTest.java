package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.Student;
import com.student.management.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class StudentControllerTest {

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

    @Test
    @DisplayName("测试更新学生 - 正常场景")
    void testUpdate() {
        // 准备测试数据
        Student student = new Student();
        student.setId(1L);
        student.setName("张三");
        student.setStudentNo("2024001");
        student.setGender("男");
        student.setPhone("13800138000");
        student.setEmail("zhangsan@example.com");

        // 执行测试
        Result<Void> result = studentController.update(student);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证service方法被调用
        verify(studentService).update(student);
    }

    @Test
    @DisplayName("测试更新学生 - ID不存在")
    void testUpdateNonExistentId() {
        // 准备测试数据 - 使用不存在的ID
        Student student = new Student();
        student.setId(999L);
        student.setName("张三");

        // 执行测试
        Result<Void> result = studentController.update(student);

        // 验证结果
        assertNotNull(result);
        assertEquals(200, result.getCode());

        // 验证service方法被调用
        verify(studentService).update(student);
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

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  "})
    @DisplayName("测试学生学号 - 空值场景")
    void testStudentNoEmpty(String studentNo) {
        Student student = new Student();
        student.setStudentNo(studentNo);
        student.setName("张三");

        Result<Void> result = studentController.add(student);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(studentService).add(student);
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