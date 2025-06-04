package com.student.management.service;

import com.student.management.entity.Class;
import java.util.List;

/**
 * 班级管理服务接口
 */
public interface ClassService {
    /**
     * 获取班级列表
     * @param className 班级名称（可选）
     * @param grade 年级（可选）
     * @param major 专业（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 班级列表
     */
    List<Class> getList(String className, String grade, String major, Integer pageNum, Integer pageSize);

    /**
     * 获取班级总数
     * @param className 班级名称（可选）
     * @param grade 年级（可选）
     * @param major 专业（可选）
     * @return 班级总数
     */
    int getCount(String className, String grade, String major);

    /**
     * 根据ID获取班级信息
     * @param id 班级ID
     * @return 班级信息
     */
    Class getById(Long id);

    /**
     * 添加班级
     * @param clazz 班级信息
     */
    void add(Class clazz);

    /**
     * 更新班级信息
     * @param clazz 班级信息
     */
    void update(Class clazz);

    /**
     * 更新班级学生数量
     * @param id 班级ID
     * @param count 学生数量
     */
    void updateStudentCount(Long id, Integer count);

    /**
     * 导出班级信息到Excel
     * @return Excel文件的字节数组
     */
    byte[] exportExcel();

    /**
     * 批量删除班级
     * @param ids 班级ID列表
     */
    void batchDelete(List<Long> ids);
} 