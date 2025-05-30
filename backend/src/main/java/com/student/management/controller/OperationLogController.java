package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.OperationLog;
import com.student.management.service.OperationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
public class OperationLogController {
    
    private static final Logger logger = LoggerFactory.getLogger(OperationLogController.class);

    @Autowired
    private OperationLogService operationLogService;

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        logger.info("获取操作日志列表，参数：operationType={}, username={}, startTime={}, endTime={}, pageNum={}, pageSize={}", 
            operationType, username, startTime, endTime, pageNum, pageSize);
        
        Map<String, Object> data = operationLogService.getList(operationType, username, startTime, endTime, pageNum, pageSize);
        return Result.success(data);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        logger.info("删除操作日志，id={}", id);
        operationLogService.deleteLog(id);
        return Result.success(null);
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        logger.info("批量删除操作日志，ids={}", ids);
        operationLogService.batchDelete(ids);
        return Result.success(null);
    }
} 