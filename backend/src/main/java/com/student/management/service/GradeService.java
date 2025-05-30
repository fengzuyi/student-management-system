package com.student.management.service;

import com.student.management.dto.GradeDTO;
import com.student.management.entity.Course;
import com.student.management.entity.Grade;
import com.student.management.entity.Student;
import com.student.management.mapper.CourseMapper;
import com.student.management.mapper.GradeMapper;
import com.student.management.mapper.StudentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService {
    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Transactional
    public GradeDTO addGrade(GradeDTO gradeDTO) {
        Student student = studentMapper.selectById(gradeDTO.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }
        
        Course course = courseMapper.selectById(gradeDTO.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 检查是否已存在该学生该课程该学期的成绩
        Grade existingGrade = gradeMapper.findByStudentIdAndCourseIdAndSemester(
            gradeDTO.getStudentId(), gradeDTO.getCourseId(), gradeDTO.getSemester());
        if (existingGrade != null) {
            throw new RuntimeException("该学生该课程该学期的成绩已存在");
        }

        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setCourse(course);
        grade.setScore(gradeDTO.getScore());
        grade.setSemester(gradeDTO.getSemester());

        gradeMapper.insert(grade);
        return convertToDTO(grade);
    }

    @Transactional
    public GradeDTO updateGrade(Long id, GradeDTO gradeDTO) {
        Grade grade = gradeMapper.selectById(id);
        if (grade == null) {
            throw new RuntimeException("成绩记录不存在");
        }

        grade.setScore(gradeDTO.getScore());
        grade.setSemester(gradeDTO.getSemester());

        gradeMapper.update(grade);
        return convertToDTO(grade);
    }

    public List<GradeDTO> getGradesByStudent(Long studentId) {
        return gradeMapper.findByStudentId(studentId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<GradeDTO> getGradesByCourse(Long courseId) {
        return gradeMapper.findByCourseId(courseId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public List<GradeDTO> getGradesByStudentAndSemester(Long studentId, String semester) {
        return gradeMapper.findByStudentIdAndSemester(studentId, semester).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    public Double getAverageScoreByCourseAndSemester(Long courseId, String semester) {
        return gradeMapper.getAverageScoreByCourseAndSemester(courseId, semester);
    }

    public Double getMaxScoreByCourseAndSemester(Long courseId, String semester) {
        return gradeMapper.getMaxScoreByCourseAndSemester(courseId, semester);
    }

    public Double getMinScoreByCourseAndSemester(Long courseId, String semester) {
        return gradeMapper.getMinScoreByCourseAndSemester(courseId, semester);
    }

    private GradeDTO convertToDTO(Grade grade) {
        GradeDTO dto = new GradeDTO();
        BeanUtils.copyProperties(grade, dto);
        dto.setStudentId(grade.getStudent().getId());
        dto.setStudentName(grade.getStudent().getName());
        dto.setStudentNo(grade.getStudent().getStudentNo());
        dto.setCourseId(grade.getCourse().getId());
        dto.setCourseName(grade.getCourse().getCourseName());
        dto.setCourseCode(grade.getCourse().getCourseCode());
        return dto;
    }
} 