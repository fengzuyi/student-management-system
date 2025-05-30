package com.student.management.controller;

import com.student.management.common.Result;
import com.student.management.dto.GradeDTO;
import com.student.management.service.GradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;

@RestController
@RequestMapping("/api/grades")
public class GradeController {
    private static final Logger logger = LoggerFactory.getLogger(GradeController.class);

    @Autowired
    private GradeService gradeService;

    @PostMapping
    public Result<GradeDTO> addGrade(@RequestBody GradeDTO gradeDTO) {
        logger.info("添加成绩：{}", gradeDTO);
        try {
            GradeDTO result = gradeService.addGrade(gradeDTO);
            return Result.success(result);
        } catch (RuntimeException e) {
            logger.error("添加成绩失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<GradeDTO> updateGrade(@PathVariable Long id, @RequestBody GradeDTO gradeDTO) {
        logger.info("更新成绩：id={}, gradeDTO={}", id, gradeDTO);
        try {
            GradeDTO result = gradeService.updateGrade(id, gradeDTO);
            return Result.success(result);
        } catch (RuntimeException e) {
            logger.error("更新成绩失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/student/{studentId}")
    public Result<List<GradeDTO>> getGradesByStudent(@PathVariable Long studentId) {
        logger.info("获取学生成绩：studentId={}", studentId);
        try {
            List<GradeDTO> grades = gradeService.getGradesByStudent(studentId);
            return Result.success(grades);
        } catch (Exception e) {
            logger.error("获取学生成绩失败：{}", e.getMessage());
            return Result.error("获取学生成绩失败");
        }
    }

    @GetMapping("/course/{courseId}")
    public Result<List<GradeDTO>> getGradesByCourse(@PathVariable Long courseId) {
        logger.info("获取课程成绩：courseId={}", courseId);
        try {
            List<GradeDTO> grades = gradeService.getGradesByCourse(courseId);
            return Result.success(grades);
        } catch (Exception e) {
            logger.error("获取课程成绩失败：{}", e.getMessage());
            return Result.error("获取课程成绩失败");
        }
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    public Result<List<GradeDTO>> getGradesByStudentAndSemester(
            @PathVariable Long studentId,
            @PathVariable String semester) {
        logger.info("获取学生学期成绩：studentId={}, semester={}", studentId, semester);
        try {
            List<GradeDTO> grades = gradeService.getGradesByStudentAndSemester(studentId, semester);
            return Result.success(grades);
        } catch (Exception e) {
            logger.error("获取学生学期成绩失败：{}", e.getMessage());
            return Result.error("获取学生学期成绩失败");
        }
    }

    @GetMapping("/course/{courseId}/semester/{semester}/statistics")
    public Result<Map<String, Double>> getCourseStatistics(
            @PathVariable Long courseId,
            @PathVariable String semester) {
        logger.info("获取课程统计信息：courseId={}, semester={}", courseId, semester);
        try {
            Double average = gradeService.getAverageScoreByCourseAndSemester(courseId, semester);
            Double max = gradeService.getMaxScoreByCourseAndSemester(courseId, semester);
            Double min = gradeService.getMinScoreByCourseAndSemester(courseId, semester);

            Map<String, Double> statistics = new HashMap<>();
            statistics.put("average", average != null ? average : 0.0);
            statistics.put("max", max != null ? max : 0.0);
            statistics.put("min", min != null ? min : 0.0);

            return Result.success(statistics);
        } catch (Exception e) {
            logger.error("获取课程统计信息失败：{}", e.getMessage());
            return Result.error("获取课程统计信息失败");
        }
    }

    @GetMapping
    public Result<Map<String, Object>> getAllGrades(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId) {
        logger.info("获取所有成绩：pageNum={}, pageSize={}, semester={}, courseId={}", 
            pageNum, pageSize, semester, courseId);
        try {
            List<GradeDTO> grades = gradeService.getAllGrades(pageNum, pageSize, semester, courseId);
            int total = gradeService.getTotalCount(semester, courseId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("list", grades);
            result.put("total", total);
            
            return Result.success(result);
        } catch (Exception e) {
            logger.error("获取所有成绩失败：{}", e.getMessage());
            return Result.error("获取所有成绩失败");
        }
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteGrade(@PathVariable Long id) {
        logger.info("删除成绩：id={}", id);
        try {
            // 使用批量删除接口，传入单个ID
            gradeService.batchDelete(Collections.singletonList(id));
            return Result.success(null);
        } catch (Exception e) {
            logger.error("删除成绩失败：{}", e.getMessage());
            return Result.error("删除成绩失败");
        }
    }

    @DeleteMapping("/batch")
    public Result<Void> batchDelete(@RequestBody Map<String, List<Long>> params) {
        List<Long> ids = params.get("ids");
        if (ids == null || ids.isEmpty()) {
            return Result.error("请选择要删除的成绩记录");
        }
        logger.info("批量删除成绩：ids={}", ids);
        try {
            gradeService.batchDelete(ids);
            return Result.success(null);
        } catch (Exception e) {
            logger.error("批量删除成绩失败：{}", e.getMessage());
            return Result.error("批量删除成绩失败");
        }
    }

    @PostMapping("/import")
    public Result<Void> importGrades(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return Result.error("请选择要导入的文件");
        }
        gradeService.importGrades(file);
        return Result.success(null);
    }

    @GetMapping("/export")
    public void exportGrades(
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) Long courseId,
            HttpServletResponse response) {
        gradeService.exportGrades(semester, courseId, response);
    }

    @GetMapping("/studentNo/{studentNo}")
    public Result<List<GradeDTO>> getGradesByStudentNo(@PathVariable String studentNo) {
        logger.info("根据学号获取成绩：studentNo={}", studentNo);
        try {
            List<GradeDTO> grades = gradeService.getGradesByStudentNo(studentNo);
            return Result.success(grades);
        } catch (Exception e) {
            logger.error("根据学号获取成绩失败：{}", e.getMessage());
            return Result.error("根据学号获取成绩失败");
        }
    }

    @GetMapping("/semesters")
    public Result<List<String>> getAllSemesters() {
        logger.info("获取所有学期列表");
        try {
            List<String> semesters = gradeService.getAllSemesters();
            return Result.success(semesters);
        } catch (Exception e) {
            logger.error("获取学期列表失败：{}", e.getMessage());
            return Result.error("获取学期列表失败");
        }
    }
} 