package com.student.management.mapper;

import com.student.management.entity.Grade;
import com.student.management.dto.GradeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface GradeMapper {
    List<GradeDTO> findAll(@Param("offset") int offset, @Param("limit") int limit);
    
    int countTotal();
    
    List<GradeDTO> findByStudentId(Long studentId);
    
    List<GradeDTO> findByCourseId(Long courseId);
    
    List<GradeDTO> findByStudentIdAndSemester(@Param("studentId") Long studentId, @Param("semester") String semester);
    
    GradeDTO findByStudentIdAndCourseIdAndSemester(
        @Param("studentId") Long studentId,
        @Param("courseId") Long courseId,
        @Param("semester") String semester
    );
    
    GradeDTO selectById(Long id);
    
    int insert(Grade grade);
    
    int update(Grade grade);
    
    int deleteById(Long id);
    
    Double getAverageScoreByCourseAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);
    
    Double getMaxScoreByCourseAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);
    
    Double getMinScoreByCourseAndSemester(@Param("courseId") Long courseId, @Param("semester") String semester);
    
    int deleteByStudentIds(@Param("studentIds") List<Long> studentIds);

    /**
     * 批量删除成绩记录
     * @param ids 成绩ID列表
     */
    void batchDelete(@Param("ids") List<Long> ids);

    /**
     * 根据课程ID列表删除相关成绩记录
     * @param courseIds 课程ID列表
     */
    void deleteByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 根据学号查询学生ID
     * @param studentNo 学号
     * @return 学生ID
     */
    Long findStudentIdByStudentNo(@Param("studentNo") String studentNo);

    /**
     * 根据课程代码查询课程ID
     * @param courseCode 课程代码
     * @return 课程ID
     */
    Long findCourseIdByCourseCode(@Param("courseCode") String courseCode);

    /**
     * 查询所有成绩记录（不分页）
     * @return 成绩记录列表
     */
    List<GradeDTO> findAllWithoutPagination();

    /**
     * 获取所有学期列表
     * @return 学期列表
     */
    List<String> findAllSemesters();

    List<GradeDTO> findAllWithFilters(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("semester") String semester,
        @Param("courseId") Long courseId
    );

    int countTotalWithFilters(
        @Param("semester") String semester,
        @Param("courseId") Long courseId
    );

    /**
     * 分批获取导出数据
     * @param offset 偏移量
     * @param limit 每批数量
     * @param semester 学期（可选）
     * @param courseId 课程ID（可选）
     * @return 成绩数据列表
     */
    List<GradeDTO> findBatchForExport(
        @Param("offset") int offset,
        @Param("limit") int limit,
        @Param("semester") String semester,
        @Param("courseId") Long courseId
    );
} 