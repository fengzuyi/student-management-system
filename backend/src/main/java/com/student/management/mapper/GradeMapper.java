package com.student.management.mapper;

import com.student.management.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GradeMapper {
    List<Grade> findByStudentId(Long studentId);
    
    List<Grade> findByCourseId(Long courseId);
    
    List<Grade> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);
    
    Grade findByStudentIdAndCourseIdAndSemester(
        @Param("studentId") Long studentId,
        @Param("courseId") Long courseId,
        @Param("semester") String semester
    );
    
    Grade selectById(Long id);
    
    int insert(Grade grade);
    
    int update(Grade grade);
    
    int deleteById(Long id);
    
    Double getAverageScoreByCourseAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);
    
    Double getMaxScoreByCourseAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);
    
    Double getMinScoreByCourseAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);
    
    int deleteByStudentIds(@Param("studentIds") List<Long> studentIds);
} 