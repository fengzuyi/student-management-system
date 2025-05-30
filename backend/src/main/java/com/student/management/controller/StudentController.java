package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.entity.Student;
import com.student.management.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/student")
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private StudentService studentService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) String studentNo,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long classId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        logger.info("获取学生列表，参数：studentNo={}, name={}, classId={}, pageNum={}, pageSize={}", 
            studentNo, name, classId, pageNum, pageSize);
        
        List<Student> list = studentService.getList(studentNo, name, classId, pageNum, pageSize);
        int total = studentService.getCount(studentNo, name, classId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        data.put("total", total);
        
        logger.info("学生列表获取成功，共{}条记录", total);
        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        logger.info("获取学生信息，id={}", id);
        return Result.success(studentService.getById(id));
    }

    @PostMapping
    public Result<Void> add(@RequestBody Student student) {
        logger.info("新增学生：{}", student);
        studentService.add(student);
        return Result.success(null);
    }

    @PutMapping
    public Result<Void> update(@RequestBody Student student) {
        logger.info("更新学生：{}", student);
        studentService.update(student);
        return Result.success(null);
    }

    @DeleteMapping("/delete")
    public Result<Void> delete(@RequestBody Map<String, List<Long>> request) {
        List<Long> ids = request.get("ids");
        logger.info("删除学生，ids={}", ids);
        studentService.delete(ids);
        return Result.success(null);
    }

    @PostMapping("/import")
    public ResponseEntity<?> importExcel(@RequestParam("file") MultipartFile file) {
        logger.info("导入学生信息");
        Map<String, Object> result = studentService.importExcel(file);
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportExcel() {
        logger.info("导出学生Excel");
        byte[] excelData = studentService.exportExcel();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            String fileName = URLEncoder.encode("学生信息.xlsx", StandardCharsets.UTF_8.toString());
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName);
        } catch (UnsupportedEncodingException e) {
            logger.error("文件名编码失败", e);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_info.xlsx");
        }
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(excelData);
    }

    @GetMapping("/template")
    public ResponseEntity<byte[]> downloadTemplate() {
        logger.info("下载学生信息导入模板");
        byte[] templateData = studentService.generateTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        try {
            String fileName = URLEncoder.encode("学生信息导入模板.xlsx", StandardCharsets.UTF_8.toString());
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + fileName);
        } catch (UnsupportedEncodingException e) {
            logger.error("文件名编码失败", e);
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=student_template.xlsx");
        }
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(templateData);
    }
} 