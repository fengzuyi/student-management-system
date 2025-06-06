package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.Class;
import com.student.management.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/class")
public class ClassController {
    private static final Logger logger = LoggerFactory.getLogger(ClassController.class);

    @Autowired
    private ClassService classService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String className,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String major,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("获取班级列表，参数：className={}, grade={}, major={}, pageNum={}, pageSize={}", 
            className, grade, major, pageNum, pageSize);
        
        List<Class> list = classService.getList(className, grade, major, pageNum, pageSize);
        int total = classService.getCount(className, grade, major);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        
        logger.info("班级列表获取成功，共{}条记录", total);
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Class> getById(@PathVariable Long id) {
        logger.info("获取班级信息，id={}", id);
        return Result.success(classService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Class clazz) {
        logger.info("新增班级：{}", clazz);
        classService.add(clazz);
        return Result.success(null);
    }

    @PutMapping
    public Result<Void> update(@RequestBody Class clazz) {
        logger.info("更新班级：{}", clazz);
        classService.update(clazz);
        return Result.success(null);
    }

    @DeleteMapping("/{ids}")
    public Result<Void> delete(@PathVariable String ids) {
        logger.info("删除班级，ids={}", ids);
        try {
            List<Long> idList = Arrays.stream(ids.split(","))
                .map(String::trim)
                .map(Long::parseLong)
                .collect(Collectors.toList());
            
            if (idList.isEmpty()) {
                return Result.error("请选择要删除的班级");
            }
            
            classService.batchDelete(idList);
            return Result.success(null);
        } catch (NumberFormatException e) {
            logger.error("删除班级失败：ID格式不正确", e);
            return Result.error("删除班级失败：ID格式不正确");
        } catch (Exception e) {
            logger.error("删除班级失败：{}", e.getMessage());
            return Result.error("删除班级失败：" + e.getMessage());
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        logger.info("导出班级Excel");
        byte[] excelData = classService.exportExcel();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            String fileName = URLEncoder.encode("班级信息.xlsx", StandardCharsets.UTF_8.toString());
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName);
        } catch (UnsupportedEncodingException e) {
            logger.error("文件名编码失败", e);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=class_info.xlsx");
        }
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(excelData);
    }
} 