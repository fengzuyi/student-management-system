package com.student.management.service.impl;

import com.student.management.entity.Class;
import com.student.management.entity.Student;
import com.student.management.mapper.ClassMapper;
import com.student.management.mapper.StudentMapper;
import com.student.management.mapper.GradeMapper;
import com.student.management.service.StudentService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private ClassMapper classMapper;

    @Autowired
    private GradeMapper gradeMapper;

    @Override
    public List<Student> getList(String studentNo, String name, Long classId, Integer pageNum, Integer pageSize) {
        int offset = (pageNum - 1) * pageSize;
        return studentMapper.getList(studentNo, name, classId, offset, pageSize);
    }

    @Override
    public int getCount(String studentNo, String name, Long classId) {
        return studentMapper.getCount(studentNo, name, classId);
    }

    @Override
    public Student getById(Long id) {
        return studentMapper.selectById(id);
    }

    @Override
    public Student getByStudentNo(String studentNo) {
        return studentMapper.selectByStudentNo(studentNo);
    }

    @Override
    @Transactional
    public void add(Student student) {
        // 检查学号是否已存在
        if (studentMapper.selectByStudentNo(student.getStudentNo()) != null) {
            throw new RuntimeException("学号已存在");
        }
        // 检查班级是否存在
        Class classInfo = classMapper.selectById(student.getClassId());
        if (classInfo == null) {
            throw new RuntimeException("班级不存在");
        }
        studentMapper.insert(student);
        // 更新班级学生数量
        int count = studentMapper.getCount(null, null, classInfo.getId());
        classMapper.updateStudentCount(classInfo.getId(), count);
    }

    @Override
    @Transactional
    public void update(Student student) {
        Student existingStudent = studentMapper.selectById(student.getId());
        if (existingStudent == null) {
            throw new RuntimeException("学生不存在");
        }
        // 检查学号是否已被其他学生使用
        Student other = studentMapper.selectByStudentNo(student.getStudentNo());
        if (other != null && !other.getId().equals(student.getId())) {
            throw new RuntimeException("学号已被其他学生使用");
        }
        // 检查班级是否存在
        Class classInfo = classMapper.selectById(student.getClassId());
        if (classInfo == null) {
            throw new RuntimeException("班级不存在");
        }
        studentMapper.update(student);
        // 更新班级学生数量
        int count = studentMapper.getCount(null, null, classInfo.getId());
        classMapper.updateStudentCount(classInfo.getId(), count);
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的学生");
        }
        
        // 获取所有要删除的学生信息，用于更新班级学生数量
        Map<Long, Integer> classStudentCounts = new HashMap<>();
        for (Long id : ids) {
            Student student = studentMapper.selectById(id);
            if (student == null) {
                throw new RuntimeException("学生不存在，id: " + id);
            }
            Long classId = student.getClassId();
            classStudentCounts.merge(classId, 1, Integer::sum);
        }
        
        // 先删除相关的成绩记录
        gradeMapper.deleteByStudentIds(ids);
        
        // 批量删除学生
        studentMapper.batchDelete(ids);
        
        // 更新相关班级的学生数量
        for (Map.Entry<Long, Integer> entry : classStudentCounts.entrySet()) {
            Long classId = entry.getKey();
            int count = studentMapper.getCount(null, null, classId);
            classMapper.updateStudentCount(classId, count);
        }
    }

    @Override
    @Transactional
    public Map<String, Object> importExcel(MultipartFile file) {
        List<String> skippedStudents = new ArrayList<>();
        List<Student> importedStudents = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            // 获取表头
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel文件格式不正确，缺少表头");
            }
            // 验证表头
            String[] expectedHeaders = {"学号", "姓名", "班级", "性别", "电话", "邮箱"};
            for (int i = 0; i < expectedHeaders.length; i++) {
                Cell cell = headerRow.getCell(i);
                if (cell == null || !expectedHeaders[i].equals(getCellValueAsString(cell))) {
                    throw new RuntimeException("Excel文件格式不正确，表头不匹配");
                }
            }
            // 获取班级映射
            List<Class> classList = classMapper.selectList();
            Map<String, Long> classNameToIdMap = classList.stream()
                .collect(Collectors.toMap(Class::getClassName, Class::getId));
            // 读取数据
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Student student = new Student();
                // 学号
                Cell studentNoCell = row.getCell(0);
                if (studentNoCell == null) {
                    throw new RuntimeException("第" + (i + 1) + "行学号不能为空");
                }
                String studentNo = getCellValueAsString(studentNoCell);
                // 检查学号是否已存在
                if (studentMapper.selectByStudentNo(studentNo) != null) {
                    skippedStudents.add("第" + (i + 1) + "行学号已存在：" + studentNo);
                    continue;
                }
                student.setStudentNo(studentNo);
                // 姓名
                Cell nameCell = row.getCell(1);
                if (nameCell == null) {
                    throw new RuntimeException("第" + (i + 1) + "行姓名不能为空");
                }
                student.setName(getCellValueAsString(nameCell));
                // 班级
                Cell classCell = row.getCell(2);
                if (classCell == null) {
                    throw new RuntimeException("第" + (i + 1) + "行班级不能为空");
                }
                String className = getCellValueAsString(classCell);
                Long classId = classNameToIdMap.get(className);
                if (classId == null) {
                    throw new RuntimeException("第" + (i + 1) + "行班级不存在：" + className);
                }
                student.setClassId(classId);
                // 性别
                Cell genderCell = row.getCell(3);
                if (genderCell == null) {
                    throw new RuntimeException("第" + (i + 1) + "行性别不能为空");
                }
                String gender = getCellValueAsString(genderCell);
                if (!"男".equals(gender) && !"女".equals(gender)) {
                    throw new RuntimeException("第" + (i + 1) + "行性别格式不正确，应为'男'或'女'");
                }
                student.setGender(gender);
                // 电话
                Cell phoneCell = row.getCell(4);
                if (phoneCell != null) {
                    String phone = getCellValueAsString(phoneCell);
                    if (!phone.matches("^1[3-9]\\d{9}$")) {
                        throw new RuntimeException("第" + (i + 1) + "行电话格式不正确");
                    }
                    student.setPhone(phone);
                }
                // 邮箱
                Cell emailCell = row.getCell(5);
                if (emailCell != null) {
                    String email = getCellValueAsString(emailCell);
                    if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
                        throw new RuntimeException("第" + (i + 1) + "行邮箱格式不正确");
                    }
                    student.setEmail(email);
                }
                importedStudents.add(student);
            }
            
            // 批量保存数据
            for (Student student : importedStudents) {
                studentMapper.insert(student);
            }
            
            // 更新班级学生数量
            for (Map.Entry<String, Long> entry : classNameToIdMap.entrySet()) {
                int count = studentMapper.getCount(null, null, entry.getValue());
                classMapper.updateStudentCount(entry.getValue(), count);
            }
            
            // 返回导入结果
            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("importedCount", importedStudents.size());
            result.put("skippedCount", skippedStudents.size());
            if (!skippedStudents.isEmpty()) {
                result.put("skippedStudents", skippedStudents);
                result.put("message", "导入完成，但以下学生因学号重复被跳过：\n" + String.join("\n", skippedStudents));
            } else {
                result.put("message", "导入成功，共导入 " + importedStudents.size() + " 条数据");
            }
            return result;
            
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "导入Excel失败：" + e.getMessage());
            return result;
        }
    }

    @Override
    public byte[] exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息");
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "性别", "电话", "邮箱"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 填充数据
            List<Student> students = studentMapper.selectAll();
            for (int i = 0; i < students.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Student student = students.get(i);
                row.createCell(0).setCellValue(student.getStudentNo());
                row.createCell(1).setCellValue(student.getName());
                // 直接使用 SQL 查询中获取的班级名称
                row.createCell(2).setCellValue(student.getClassInfo() != null ? student.getClassInfo().getClassName() : "");
                row.createCell(3).setCellValue(student.getGender());
                row.createCell(4).setCellValue(student.getPhone());
                row.createCell(5).setCellValue(student.getEmail());
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

    @Override
    public byte[] generateTemplate() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息");
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "性别", "电话", "邮箱"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            
            // 添加示例数据
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("2023000001");
            exampleRow.createCell(1).setCellValue("张三");
            exampleRow.createCell(2).setCellValue("计算机科学与技术1班");
            exampleRow.createCell(3).setCellValue("男");
            exampleRow.createCell(4).setCellValue("13800138000");
            exampleRow.createCell(5).setCellValue("zhangsan@example.com");
            
            // 添加说明
            Row noteRow = sheet.createRow(3);
            Cell noteCell = noteRow.createCell(0);
            noteCell.setCellValue("说明：");
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 5));
            
            Row note1Row = sheet.createRow(4);
            Cell note1Cell = note1Row.createCell(0);
            note1Cell.setCellValue("1. 学号必须为10位数字");
            sheet.addMergedRegion(new CellRangeAddress(4, 4, 0, 5));
            
            Row note2Row = sheet.createRow(5);
            Cell note2Cell = note2Row.createCell(0);
            note2Cell.setCellValue("2. 性别只能填写\"男\"或\"女\"");
            sheet.addMergedRegion(new CellRangeAddress(5, 5, 0, 5));
            
            Row note3Row = sheet.createRow(6);
            Cell note3Cell = note3Row.createCell(0);
            note3Cell.setCellValue("3. 电话必须为11位手机号码");
            sheet.addMergedRegion(new CellRangeAddress(6, 6, 0, 5));
            
            Row note4Row = sheet.createRow(7);
            Cell note4Cell = note4Row.createCell(0);
            note4Cell.setCellValue("4. 邮箱格式必须正确");
            sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 5));
            
            Row note5Row = sheet.createRow(8);
            Cell note5Cell = note5Row.createCell(0);
            note5Cell.setCellValue("5. 班级必须存在于系统中");
            sheet.addMergedRegion(new CellRangeAddress(8, 8, 0, 5));
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // 导出为字节数组
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("生成模板失败", e);
        }
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
                    return cell.getLocalDateTimeCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
} 