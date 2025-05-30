package com.student.management.service.impl;

import com.student.management.entity.Course;
import com.student.management.mapper.CourseMapper;
import com.student.management.mapper.GradeMapper;
import com.student.management.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Override
    public List<Course> getList(String courseCode, String courseName, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return courseMapper.selectList(courseCode, courseName, offset, pageSize);
    }

    @Override
    public int getCount(String courseCode, String courseName) {
        return courseMapper.selectCount(courseCode, courseName);
    }

    @Override
    public Course getById(Long id) {
        return courseMapper.selectById(id);
    }

    @Override
    public Course getByCourseCode(String courseCode) {
        return courseMapper.selectByCourseCode(courseCode);
    }

    @Override
    @Transactional
    public void add(Course course) {
        courseMapper.insert(course);
    }

    @Override
    @Transactional
    public void update(Course course) {
        courseMapper.update(course);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的课程");
        }
        
        // 先删除相关的成绩记录
        gradeMapper.deleteByCourseIds(ids);
        
        // 再删除课程
        for (Long id : ids) {
            courseMapper.deleteById(id);
        }
    }

    @Override
    public Course findByCourseCode(String courseCode) {
        return courseMapper.findByCourseCode(courseCode);
    }
} 