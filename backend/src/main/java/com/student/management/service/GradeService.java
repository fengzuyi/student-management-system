package com.student.management.service;

import com.student.management.dto.GradeDTO;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public interface GradeService {
    /**
     * 获取所有成绩
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param semester 学期（可选）
     * @param courseId 课程ID（可选）
     * @return 成绩列表
     */
    List<GradeDTO> getAllGrades(int pageNum, int pageSize, String semester, Long courseId);

    /**
     * 获取成绩总记录数
     * @param semester 学期（可选）
     * @param courseId 课程ID（可选）
     * @return 总记录数
     */
    int getTotalCount(String semester, Long courseId);

    /**
     * 添加成绩
     */
    GradeDTO addGrade(GradeDTO gradeDTO);

    /**
     * 更新成绩
     */
    GradeDTO updateGrade(Long id, GradeDTO gradeDTO);

    /**
     * 获取学生的所有成绩
     */
    List<GradeDTO> getGradesByStudent(Long studentId);

    /**
     * 获取课程的所有成绩
     */
    List<GradeDTO> getGradesByCourse(Long courseId);

    /**
     * 获取学生指定学期的成绩
     */
    List<GradeDTO> getGradesByStudentAndSemester(Long studentId, String semester);

    /**
     * 获取课程指定学期的平均分
     */
    Double getAverageScoreByCourseAndSemester(Long courseId, String semester);

    /**
     * 获取课程指定学期的最高分
     */
    Double getMaxScoreByCourseAndSemester(Long courseId, String semester);

    /**
     * 获取课程指定学期的最低分
     */
    Double getMinScoreByCourseAndSemester(Long courseId, String semester);

    /**
     * 批量删除成绩记录
     * @param ids 成绩ID列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 导入成绩数据
     * @param file Excel文件
     */
    void importGrades(MultipartFile file);

    /**
     * 导出成绩数据
     * @param semester 学期（可选）
     * @param courseId 课程ID（可选）
     * @param response HTTP响应对象
     */
    void exportGrades(String semester, Long courseId, HttpServletResponse response);

    /**
     * 根据学号查询成绩
     * @param studentNo 学号
     * @return 成绩列表
     */
    List<GradeDTO> getGradesByStudentNo(String studentNo);

    /**
     * 获取所有学期列表
     * @return 学期列表
     */
    List<String> getAllSemesters();
} 