package com.student.management.service;

import com.student.management.entity.Course;
import java.util.List;

public interface CourseService {
    List<Course> getList(String courseCode, String courseName, Integer pageNum, Integer pageSize);
    int getCount(String courseCode, String courseName);
    Course getById(Long id);
    Course getByCourseCode(String courseCode);
    void add(Course course);
    void update(Course course);
    void delete(List<Long> ids);

    /**
     * 根据课程代码查询课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Course findByCourseCode(String courseCode);
} 