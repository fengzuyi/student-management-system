package com.student.management.mapper;

import com.student.management.entity.Class;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface ClassMapper {
    List<Class> selectList(@Param("className") String className,
                          @Param("grade") String grade,
                          @Param("major") String major,
                          @Param("offset") Integer offset,
                          @Param("limit") Integer limit);
    
    int selectCount(@Param("className") String className,
                   @Param("grade") String grade,
                   @Param("major") String major);
    
    int selectCountExcludeSelf(@Param("className") String className,
                             @Param("grade") String grade,
                             @Param("major") String major,
                             @Param("excludeId") Long excludeId);
    
    Class selectById(Long id);
    
    int insert(Class clazz);
    
    int update(Class clazz);
    
    int deleteById(Long id);
    
    int updateStudentCount(@Param("id") Long id, @Param("count") Integer count);
    
    int batchDelete(@Param("ids") List<Long> ids);
} 