package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.Course;
import com.student.management.service.CourseService;
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

class CourseControllerTest {
    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("测试获取课程列表")
    void testList() {
        List<Course> mockList = new ArrayList<>();
        Course course = new Course();
        course.setId(1L);
        course.setCourseName("高数");
        mockList.add(course);
        when(courseService.getList(anyString(), anyString(), anyInt(), anyInt())).thenReturn(mockList);
        when(courseService.getCount(anyString(), anyString())).thenReturn(1);
        Result<Map<String, Object>> result = courseController.list("MATH101", "高数", 1, 10);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(1, result.getData().get("total"));
        assertEquals(mockList, result.getData().get("list"));
    }

    @Test
    @DisplayName("测试根据ID获取课程")
    void testGetById() {
        Course course = new Course();
        course.setId(1L);
        course.setCourseName("高数");
        when(courseService.getById(1L)).thenReturn(course);
        Result<Course> result = courseController.getById(1L);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(course, result.getData());
    }

    @Test
    @DisplayName("测试新增课程")
    void testAdd() {
        Course course = new Course();
        course.setCourseName("高数");
        Result<Void> result = courseController.add(course);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(courseService).add(course);
    }

    @Test
    @DisplayName("测试更新课程")
    void testUpdate() {
        Course course = new Course();
        course.setId(1L);
        course.setCourseName("高数");
        Result<Void> result = courseController.update(course);
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(courseService).update(course);
    }

    @Test
    @DisplayName("测试删除课程")
    void testDelete() {
        Result<Void> result = courseController.delete(Arrays.asList(1L,2L));
        assertNotNull(result);
        assertEquals(200, result.getCode());
        verify(courseService).delete(anyList());
    }

    @Test
    @DisplayName("测试根据课程代码获取课程")
    void testGetCourseByCode() {
        Course course = new Course();
        course.setCourseCode("MATH101");
        when(courseService.findByCourseCode("MATH101")).thenReturn(course);
        Result<Course> result = courseController.getCourseByCode("MATH101");
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals(course, result.getData());
    }
} 