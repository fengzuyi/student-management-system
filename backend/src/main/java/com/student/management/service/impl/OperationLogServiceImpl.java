package com.student.management.service.impl;

import com.student.management.entity.OperationLog;
import com.student.management.mapper.OperationLogMapper;
import com.student.management.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    public Map<String, Object> getList(String operationType, String username, String startTime, String endTime, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<OperationLog> list = operationLogMapper.selectList(operationType, username, startTime, endTime, offset, pageSize);
        int total = operationLogMapper.selectCount(operationType, username, startTime, endTime);
        
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return result;
    }

    @Override
    @Transactional
    public void addLog(OperationLog log) {
        operationLogMapper.insert(log);
    }

    @Override
    @Transactional
    public void deleteLog(Long id) {
        operationLogMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            operationLogMapper.batchDelete(ids);
        }
    }
} 