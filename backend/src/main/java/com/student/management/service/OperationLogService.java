package com.student.management.service;

import com.student.management.entity.OperationLog;
import java.util.List;
import java.util.Map;

public interface OperationLogService {
    /**
     * 获取日志列表
     */
    Map<String, Object> getList(String operationType, String username, String startTime, String endTime, Integer pageNum, Integer pageSize);
    
    /**
     * 添加日志
     */
    void addLog(OperationLog log);
    
    /**
     * 删除日志
     */
    void deleteLog(Long id);
    
    /**
     * 批量删除日志
     */
    void batchDelete(List<Long> ids);
} 