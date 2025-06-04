package com.student.management.service;

import com.student.management.entity.Student;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

/**
 * 学生管理服务接口
 */
public interface StudentService {
    /**
     * 获取学生列表
     * @param studentNo 学号（可选）
     * @param name 姓名（可选）
     * @param classId 班级ID（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 学生列表
     */
    List<Student> getList(String studentNo, String name, Long classId, Integer pageNum, Integer pageSize);

    /**
     * 获取学生总数
     * @param studentNo 学号（可选）
     * @param name 姓名（可选）
     * @param classId 班级ID（可选）
     * @return 学生总数
     */
    int getCount(String studentNo, String name, Long classId);

    /**
     * 根据ID获取学生信息
     * @param id 学生ID
     * @return 学生信息
     */
    Student getById(Long id);

    /**
     * 根据学号获取学生信息
     * @param studentNo 学号
     * @return 学生信息
     */
    Student getByStudentNo(String studentNo);

    /**
     * 添加学生
     * @param student 学生信息
     */
    void add(Student student);

    /**
     * 更新学生信息
     * @param student 学生信息
     */
    void update(Student student);

    /**
     * 删除学生
     * @param ids 学生ID列表
     */
    void delete(List<Long> ids);

    /**
     * 从Excel导入学生信息
     * @param file Excel文件
     * @return 导入结果，包含成功数量和失败信息
     */
    Map<String, Object> importExcel(MultipartFile file);

    /**
     * 导出学生信息到Excel
     * @return Excel文件的字节数组
     */
    byte[] exportExcel();

    /**
     * 生成学生信息导入模板
     * @return Excel模板文件的字节数组
     */
    byte[] generateTemplate();
} 