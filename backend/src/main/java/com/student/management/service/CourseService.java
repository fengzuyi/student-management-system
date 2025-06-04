package com.student.management.service;

import com.student.management.entity.Course;
import java.util.List;

/**
 * 课程管理服务接口
 */
public interface CourseService {
    /**
     * 获取课程列表
     * @param courseCode 课程代码（可选）
     * @param courseName 课程名称（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 课程列表
     */
    List<Course> getList(String courseCode, String courseName, Integer pageNum, Integer pageSize);

    /**
     * 获取课程总数
     * @param courseCode 课程代码（可选）
     * @param courseName 课程名称（可选）
     * @return 课程总数
     */
    int getCount(String courseCode, String courseName);

    /**
     * 根据ID获取课程信息
     * @param id 课程ID
     * @return 课程信息
     */
    Course getById(Long id);

    /**
     * 根据课程代码获取课程信息
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Course getByCourseCode(String courseCode);

    /**
     * 添加课程
     * @param course 课程信息
     */
    void add(Course course);

    /**
     * 更新课程信息
     * @param course 课程信息
     */
    void update(Course course);

    /**
     * 删除课程
     * @param ids 课程ID列表
     */
    void delete(List<Long> ids);

    /**
     * 根据课程代码查询课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Course findByCourseCode(String courseCode);
} 