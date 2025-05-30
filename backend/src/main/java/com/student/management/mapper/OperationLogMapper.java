package com.student.management.mapper;

import com.student.management.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface OperationLogMapper {
    List<OperationLog> selectList(
        @Param("operationType") String operationType,
        @Param("username") String username,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime,
        @Param("offset") Integer offset,
        @Param("limit") Integer limit
    );
    
    int selectCount(
        @Param("operationType") String operationType,
        @Param("username") String username,
        @Param("startTime") String startTime,
        @Param("endTime") String endTime
    );
    
    void insert(OperationLog log);
    
    void deleteById(Long id);
    
    void batchDelete(@Param("ids") List<Long> ids);
} 