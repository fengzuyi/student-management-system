package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.dto.GradeDTO;
import com.student.management.service.GradeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GradeControllerTest {
    @InjectMocks
    private GradeController gradeController;

    @Mock
    private GradeService gradeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试添加成绩")
    void testAddGrade() {
        GradeDTO dto = new GradeDTO();
        when(gradeService.addGrade(any())).thenReturn(dto);
        Result<GradeDTO> result = gradeController.addGrade(dto);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(dto, result.getData());
    }

    @Test
    @DisplayName("测试更新成绩")
    void testUpdateGrade() {
        GradeDTO dto = new GradeDTO();
        when(gradeService.updateGrade(eq(1L), any())).thenReturn(dto);
        Result<GradeDTO> result = gradeController.updateGrade(1L, dto);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(dto, result.getData());
    }

    @Test
    @DisplayName("测试获取学生成绩")
    void testGetGradesByStudent() {
        List<GradeDTO> list = new ArrayList<>();
        when(gradeService.getGradesByStudent(1L)).thenReturn(list);
        Result<List<GradeDTO>> result = gradeController.getGradesByStudent(1L);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(list, result.getData());
    }

    @Test
    @DisplayName("测试获取课程成绩")
    void testGetGradesByCourse() {
        List<GradeDTO> list = new ArrayList<>();
        when(gradeService.getGradesByCourse(1L)).thenReturn(list);
        Result<List<GradeDTO>> result = gradeController.getGradesByCourse(1L);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(list, result.getData());
    }

    @Test
    @DisplayName("测试获取学生学期成绩")
    void testGetGradesByStudentAndSemester() {
        List<GradeDTO> list = new ArrayList<>();
        when(gradeService.getGradesByStudentAndSemester(1L, "2023-2024-1")).thenReturn(list);
        Result<List<GradeDTO>> result = gradeController.getGradesByStudentAndSemester(1L, "2023-2024-1");
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(list, result.getData());
    }

    @Test
    @DisplayName("测试获取课程统计信息")
    void testGetCourseStatistics() {
        when(gradeService.getAverageScoreByCourseAndSemester(1L, "2023-2024-1")).thenReturn(90.0);
        when(gradeService.getMaxScoreByCourseAndSemester(1L, "2023-2024-1")).thenReturn(100.0);
        when(gradeService.getMinScoreByCourseAndSemester(1L, "2023-2024-1")).thenReturn(60.0);
        Result<Map<String, Double>> result = gradeController.getCourseStatistics(1L, "2023-2024-1");
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(90.0, result.getData().get("average"));
        assertEquals(100.0, result.getData().get("max"));
        assertEquals(60.0, result.getData().get("min"));
    }

    @Test
    @DisplayName("测试分页获取所有成绩")
    void testGetAllGrades() {
        List<GradeDTO> list = new ArrayList<>();
        when(gradeService.getAllGrades(anyInt(), anyInt(), anyString(), any())).thenReturn(list);
        when(gradeService.getTotalCount(anyString(), any())).thenReturn(0);
        Result<Map<String, Object>> result = gradeController.getAllGrades(1, 10, null, null);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(list, result.getData().get("list"));
        assertEquals(0, result.getData().get("total"));
    }

    @Test
    @DisplayName("测试删除成绩")
    void testDeleteGrade() {
        Result<Void> result = gradeController.deleteGrade(1L);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(gradeService).batchDelete(Collections.singletonList(1L));
    }

    @Test
    @DisplayName("测试批量删除成绩")
    void testBatchDelete() {
        Map<String, List<Long>> params = new HashMap<>();
        params.put("ids", Arrays.asList(1L,2L));
        Result<Void> result = gradeController.batchDelete(params);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(gradeService).batchDelete(Arrays.asList(1L,2L));
    }

    @Test
    @DisplayName("测试导入成绩")
    void testImportGrades() {
        MockMultipartFile file = new MockMultipartFile("file", "test.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", new byte[]{1,2,3});
        Result<Void> result = gradeController.importGrades(file);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(gradeService).importGrades(file);
    }

    @Test
    @DisplayName("测试导出成绩")
    void testExportGrades() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        gradeController.exportGrades(null, null, response);
        verify(gradeService).exportGrades(null, null, response);
    }

    @Test
    @DisplayName("测试根据学号获取成绩")
    void testGetGradesByStudentNo() {
        List<GradeDTO> list = new ArrayList<>();
        when(gradeService.getGradesByStudentNo("2024001")).thenReturn(list);
        Result<List<GradeDTO>> result = gradeController.getGradesByStudentNo("2024001");
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(list, result.getData());
    }

    @Test
    @DisplayName("测试获取所有学期列表")
    void testGetAllSemesters() {
        List<String> semesters = Arrays.asList("2023-2024-1", "2023-2024-2");
        when(gradeService.getAllSemesters()).thenReturn(semesters);
        Result<List<String>> result = gradeController.getAllSemesters();
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(semesters, result.getData());
    }
} 