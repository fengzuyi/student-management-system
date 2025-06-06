package com.student.management.mapper;

import com.student.management.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CourseMapper {
    List<Course> selectList(@Param("courseCode") String courseCode,
                          @Param("courseName") String courseName,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);
    
    int selectCount(@Param("courseCode") String courseCode,
                   @Param("courseName") String courseName);
    
    Course selectById(Long id);
    
    Course selectByCourseCode(String courseCode);
    
    int insert(Course course);
    
    int update(Course course);
    
    int deleteById(Long id);

    /**
     * 根据课程代码查询课程
     * @param courseCode 课程代码
     * @return 课程信息
     */
    Course findByCourseCode(@Param("courseCode") String courseCode);
} 