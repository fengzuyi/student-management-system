package com.student.management.service.impl;

import com.student.management.entity.Class;
import com.student.management.entity.Student;
import com.student.management.mapper.ClassMapper;
import com.student.management.service.ClassService;
import com.student.management.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private StudentService studentService;

    @Override
    public List<Class> getList() {
        return classMapper.selectList();
    }

    @Override
    public Class getById(Long id) {
        Class clazz = classMapper.selectById(id);
        if (clazz == null) {
            throw new RuntimeException("班级不存在");
        }
        return clazz;
    }

    @Override
    @Transactional
    public void add(Class clazz) {
        clazz.setStudentCount(0);
        classMapper.insert(clazz);
    }

    @Override
    @Transactional
    public void update(Class clazz) {
        Class existingClass = classMapper.selectById(clazz.getId());
        if (existingClass == null) {
            throw new RuntimeException("班级不存在");
        }
        existingClass.setClassName(clazz.getClassName());
        existingClass.setGrade(clazz.getGrade());
        existingClass.setMajor(clazz.getMajor());
        classMapper.update(existingClass);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Class clazz = classMapper.selectById(id);
        if (clazz == null) {
            throw new RuntimeException("班级不存在");
        }
        // 获取该班级下的所有学生
        List<Student> students = studentService.getList(null, null, id, 1, Integer.MAX_VALUE);
        // 删除该班级下的所有学生
        for (Student student : students) {
            studentService.delete(Collections.singletonList(student.getId()));
        }
        // 删除班级
        classMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的班级");
        }
        
        // 获取所有要删除的班级信息
        for (Long id : ids) {
            Class clazz = classMapper.selectById(id);
            if (clazz == null) {
                throw new RuntimeException("班级不存在，id: " + id);
            }
            // 获取该班级下的所有学生
            List<Student> students = studentService.getList(null, null, id, 1, Integer.MAX_VALUE);
            // 删除该班级下的所有学生
            for (Student student : students) {
                studentService.delete(Collections.singletonList(student.getId()));
            }
        }
        
        // 批量删除班级
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
            List<Class> classList = getList();
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
            // 导出为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("导出Excel失败", e);
        }
    }
} 