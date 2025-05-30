package com.student.management.controller;

import com.student.management.dto.GradeDTO;
import com.student.management.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
@CrossOrigin(origins = "*")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    @PostMapping
    public ResponseEntity<GradeDTO> addGrade(@RequestBody GradeDTO gradeDTO) {
        return ResponseEntity.ok(gradeService.addGrade(gradeDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GradeDTO> updateGrade(@PathVariable Long id, @RequestBody GradeDTO gradeDTO) {
        return ResponseEntity.ok(gradeService.updateGrade(id, gradeDTO));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeDTO>> getGradesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(gradeService.getGradesByStudent(studentId));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<GradeDTO>> getGradesByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(gradeService.getGradesByCourse(courseId));
    }

    @GetMapping("/student/{studentId}/semester/{semester}")
    public ResponseEntity<List<GradeDTO>> getGradesByStudentAndSemester(
            @PathVariable Long studentId,
            @PathVariable String semester) {
        return ResponseEntity.ok(gradeService.getGradesByStudentAndSemester(studentId, semester));
    }

    @GetMapping("/course/{courseId}/semester/{semester}/statistics")
    public ResponseEntity<Map<String, Double>> getCourseStatistics(
            @PathVariable Long courseId,
            @PathVariable String semester) {
        Double average = gradeService.getAverageScoreByCourseAndSemester(courseId, semester);
        Double max = gradeService.getMaxScoreByCourseAndSemester(courseId, semester);
        Double min = gradeService.getMinScoreByCourseAndSemester(courseId, semester);

        return ResponseEntity.ok(Map.of(
            "average", average != null ? average : 0.0,
            "max", max != null ? max : 0.0,
            "min", min != null ? min : 0.0
        ));
    }
} 