package com.student.management.service.impl;

import com.student.management.entity.Class;
import com.student.management.entity.Student;
import com.student.management.mapper.ClassMapper;
import com.student.management.mapper.StudentMapper;
import com.student.management.mapper.GradeMapper;
import com.student.management.service.ClassService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Override
    public List<Class> getList(String className, String grade, String major, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return classMapper.selectList(className, grade, major, offset, pageSize);
    }

    @Override
    public int getCount(String className, String grade, String major) {
        return classMapper.selectCount(className, grade, major);
    }

    @Override
    public Class getById(Long id) {
        return classMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(Class clazz) {
        // 检查班级名称是否已存在
        if (classMapper.selectCount(clazz.getClassName(), null, null) > 0) {
            throw new RuntimeException("班级名称已存在");
        }
        classMapper.insert(clazz);
    }

    @Override
    @Transactional
    public void update(Class clazz) {
        // 检查班级名称是否已存在（排除自身）
        if (classMapper.selectCountExcludeSelf(clazz.getClassName(), null, null, clazz.getId()) > 0) {
            throw new RuntimeException("班级名称已存在");
        }
        classMapper.update(clazz);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的班级");
        }
        
        // 获取所有要删除的班级下的学生ID
        List<Long> allStudentIds = new ArrayList<>();
        for (Long classId : ids) {
            List<Student> students = studentMapper.selectByClassId(classId);
            if (!students.isEmpty()) {
                List<Long> studentIds = students.stream()
                    .map(Student::getId)
                    .collect(Collectors.toList());
                allStudentIds.addAll(studentIds);
            }
        }
        
        // 如果有学生，先删除相关的成绩记录
        if (!allStudentIds.isEmpty()) {
            gradeMapper.deleteByStudentIds(allStudentIds);
            // 删除学生记录
            studentMapper.batchDelete(allStudentIds);
        }
        
        // 最后删除班级
        classMapper.batchDelete(ids);
    }

    @Override
    @Transactional
    public void updateStudentCount(Long id, Integer count) {
        classMapper.updateStudentCount(id, count);
    }

    @Override
    public byte[] exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("班级信息");
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"班级名称", "年级", "专业", "学生人数"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 填充数据
            List<Class> classList = getList(null, null, null, 1, Integer.MAX_VALUE);
            for (int i = 0; i < classList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Class clazz = classList.get(i);
                row.createCell(0).setCellValue(clazz.getClassName());
                row.createCell(1).setCellValue(clazz.getGrade());
                row.createCell(2).setCellValue(clazz.getMajor());
                row.createCell(3).setCellValue(clazz.getStudentCount());
            }
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            // 输出Excel文件
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }
} 