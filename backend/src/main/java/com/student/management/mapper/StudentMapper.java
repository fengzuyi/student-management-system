package com.student.management.mapper;

import com.student.management.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface StudentMapper {
    List<Student> getList(@Param("studentNo") String studentNo,
                         @Param("name") String name,
                         @Param("classId") Long classId,
                         @Param("offset") Integer offset,
                         @Param("limit") Integer limit);
    
    int getCount(@Param("studentNo") String studentNo,
                @Param("name") String name,
                @Param("classId") Long classId);
    
    Student selectById(Long id);
    
    Student selectByStudentNo(String studentNo);
    
    int insert(Student student);
    
    int update(Student student);
    
    int deleteById(Long id);
    
    int batchDelete(@Param("ids") List<Long> ids);
    
    List<Student> selectByClassId(Long classId);
    
    List<Student> selectAll();
} 