package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.Course;
import com.student.management.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/course")
public class CourseController {
    
    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String courseCode,
            @RequestParam(required = false) String courseName,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        List<Course> courses = courseService.getList(courseCode, courseName, pageNum, pageSize);
        int total = courseService.getCount(courseCode, courseName);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", courses);
        data.put("total", total);
        
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Course> getById(@PathVariable Long id) {
        Course course = courseService.getById(id);
        return Result.success(course);
    }

    @PostMapping
    public Result<Void> add(@RequestBody Course course) {
        courseService.add(course);
        return Result.success(null);
    }

    @PutMapping
    public Result<Void> update(@RequestBody Course course) {
        courseService.update(course);
        return Result.success(null);
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable List<Long> ids) {
        courseService.delete(ids);
        return Result.success(null);
    }
} 