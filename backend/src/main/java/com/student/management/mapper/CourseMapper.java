package com.student.management.mapper;

import com.student.management.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CourseMapper {
    List<Course> selectList();
    
    Course selectById(Long id);
    
    Course selectByCourseCode(String courseCode);
    
    int insert(Course course);
    
    int update(Course course);
    
    int deleteById(Long id);
} 