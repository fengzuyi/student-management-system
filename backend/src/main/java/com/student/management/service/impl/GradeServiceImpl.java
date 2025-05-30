package com.student.management.service.impl;

import com.student.management.dto.GradeDTO;
import com.student.management.entity.Grade;
import com.student.management.entity.Student;
import com.student.management.entity.Course;
import com.student.management.mapper.GradeMapper;
import com.student.management.mapper.StudentMapper;
import com.student.management.mapper.CourseMapper;
import com.student.management.service.GradeService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GradeServiceImpl implements GradeService {

    @Autowired
    private GradeMapper gradeMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private CourseMapper courseMapper;

    @Override
    @Transactional
    public GradeDTO addGrade(GradeDTO gradeDTO) {
        // 验证学生是否存在
        Student student = studentMapper.selectById(gradeDTO.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 验证课程是否存在
        Course course = courseMapper.selectById(gradeDTO.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 验证是否已存在该学生该课程该学期的成绩
        GradeDTO existingGrade = gradeMapper.findByStudentIdAndCourseIdAndSemester(
            gradeDTO.getStudentId(),
            gradeDTO.getCourseId(),
            gradeDTO.getSemester()
        );
        if (existingGrade != null) {
            throw new RuntimeException("该学生该课程该学期的成绩已存在");
        }

        // 转换为实体类并保存
        Grade grade = new Grade();
        BeanUtils.copyProperties(gradeDTO, grade);
        gradeMapper.insert(grade);

        // 返回保存后的完整信息
        return gradeMapper.selectById(grade.getId());
    }

    @Override
    @Transactional
    public GradeDTO updateGrade(Long id, GradeDTO gradeDTO) {
        // 验证成绩是否存在
        GradeDTO existingGrade = gradeMapper.selectById(id);
        if (existingGrade == null) {
            throw new RuntimeException("成绩不存在");
        }

        // 验证学生是否存在
        Student student = studentMapper.selectById(gradeDTO.getStudentId());
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        // 验证课程是否存在
        Course course = courseMapper.selectById(gradeDTO.getCourseId());
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        // 验证是否与其他成绩记录冲突
        GradeDTO conflictGrade = gradeMapper.findByStudentIdAndCourseIdAndSemester(
            gradeDTO.getStudentId(),
            gradeDTO.getCourseId(),
            gradeDTO.getSemester()
        );
        if (conflictGrade != null && !conflictGrade.getId().equals(id)) {
            throw new RuntimeException("该学生该课程该学期的成绩已存在");
        }

        // 转换为实体类并更新
        Grade grade = new Grade();
        BeanUtils.copyProperties(gradeDTO, grade);
        grade.setId(id);
        gradeMapper.update(grade);

        // 返回更新后的完整信息
        return gradeMapper.selectById(id);
    }

    @Override
    public List<GradeDTO> getGradesByStudent(Long studentId) {
        // 验证学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        return gradeMapper.findByStudentId(studentId);
    }

    @Override
    public List<GradeDTO> getGradesByStudentNo(String studentNo) {
        // 根据学号查询学生ID
        Long studentId = gradeMapper.findStudentIdByStudentNo(studentNo);
        if (studentId == null) {
            throw new RuntimeException("学号不存在");
        }
        return gradeMapper.findByStudentId(studentId);
    }

    @Override
    public List<GradeDTO> getGradesByCourse(Long courseId) {
        // 验证课程是否存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new RuntimeException("课程不存在");
        }

        return gradeMapper.findByCourseId(courseId);
    }

    @Override
    public List<GradeDTO> getGradesByStudentAndSemester(Long studentId, String semester) {
        // 验证学生是否存在
        Student student = studentMapper.selectById(studentId);
        if (student == null) {
            throw new RuntimeException("学生不存在");
        }

        return gradeMapper.findByStudentIdAndSemester(studentId, semester);
    }

    @Override
    public Double getAverageScoreByCourseAndSemester(Long courseId, String semester) {
        return gradeMapper.getAverageScoreByCourseAndSemester(courseId, semester);
    }

    @Override
    public Double getMaxScoreByCourseAndSemester(Long courseId, String semester) {
        return gradeMapper.getMaxScoreByCourseAndSemester(courseId, semester);
    }

    @Override
    public Double getMinScoreByCourseAndSemester(Long courseId, String semester) {
        return gradeMapper.getMinScoreByCourseAndSemester(courseId, semester);
    }

    @Override
    public List<GradeDTO> getAllGrades(int pageNum, int pageSize, String semester, Long courseId) {
        int offset = (pageNum - 1) * pageSize;
        return gradeMapper.findAllWithFilters(offset, pageSize, semester, courseId);
    }

    @Override
    public int getTotalCount(String semester, Long courseId) {
        return gradeMapper.countTotalWithFilters(semester, courseId);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            gradeMapper.batchDelete(ids);
        }
    }

    @Override
    @Transactional
    public void importGrades(MultipartFile file) {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过表头
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Grade grade = new Grade();
                
                // 读取学号
                Cell studentNoCell = row.getCell(0);
                if (studentNoCell != null) {
                    String studentNo = getCellValueAsString(studentNoCell);
                    // 根据学号查询学生ID
                    Long studentId = gradeMapper.findStudentIdByStudentNo(studentNo);
                    if (studentId != null) {
                        grade.setStudentId(studentId);
                    }
                }

                // 读取课程代码
                Cell courseCodeCell = row.getCell(1);
                if (courseCodeCell != null) {
                    String courseCode = getCellValueAsString(courseCodeCell);
                    // 根据课程代码查询课程ID
                    Long courseId = gradeMapper.findCourseIdByCourseCode(courseCode);
                    if (courseId != null) {
                        grade.setCourseId(courseId);
                    }
                }

                // 读取学期
                Cell semesterCell = row.getCell(2);
                if (semesterCell != null) {
                    grade.setSemester(getCellValueAsString(semesterCell));
                }

                // 读取成绩
                Cell scoreCell = row.getCell(3);
                if (scoreCell != null) {
                    try {
                        String scoreStr = getCellValueAsString(scoreCell);
                        grade.setScore(new java.math.BigDecimal(scoreStr));
                    } catch (NumberFormatException e) {
                        // 忽略无效的成绩数据
                        continue;
                    }
                }

                // 如果所有必要字段都有值，则保存成绩
                if (grade.getStudentId() != null && grade.getCourseId() != null 
                    && grade.getSemester() != null && grade.getScore() != null) {
                    gradeMapper.insert(grade);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("导入成绩失败", e);
        }
    }

    @Override
    public void exportGrades(String semester, Long courseId, HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", 
                "attachment; filename=" + URLEncoder.encode("成绩列表.xlsx", StandardCharsets.UTF_8));
            
            // 使用 SXSSFWorkbook 来处理大数据量
            try (SXSSFWorkbook workbook = new SXSSFWorkbook(100)) { // 每100行刷新一次内存
                Sheet sheet = workbook.createSheet("成绩列表");
                
                // 创建表头
                Row headerRow = sheet.createRow(0);
                String[] headers = {"学号", "姓名", "课程代码", "课程名称", "学期", "成绩"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                // 分批获取数据并写入
                int batchSize = 1000;
                int offset = 0;
                int rowNum = 1;
                
                while (true) {
                    List<GradeDTO> batch = gradeMapper.findBatchForExport(offset, batchSize, semester, courseId);
                    if (batch.isEmpty()) {
                        break;
                    }

                    // 写入当前批次的数据
                    for (GradeDTO grade : batch) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(grade.getStudentNo());
                        row.createCell(1).setCellValue(grade.getStudentName());
                        row.createCell(2).setCellValue(grade.getCourseCode());
                        row.createCell(3).setCellValue(grade.getCourseName());
                        row.createCell(4).setCellValue(grade.getSemester());
                        row.createCell(5).setCellValue(grade.getScore().doubleValue());
                    }

                    offset += batchSize;
                }

                // 一次性写入响应流
                workbook.write(response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (IOException e) {
            throw new RuntimeException("导出成绩失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getAllSemesters() {
        return gradeMapper.findAllSemesters();
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
} 