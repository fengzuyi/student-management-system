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
        // 检查班级是否存在
        Class clazz = classMapper.selectById(student.getClassId());
        if (clazz == null) {
            throw new RuntimeException("班级不存在");
        }
        
        // 检查学号是否已存在
        Student existingStudent = studentMapper.selectByStudentNo(student.getStudentNo());
        if (existingStudent != null) {
            throw new RuntimeException("学号已存在");
        }
        
        // 插入学生信息
        studentMapper.insert(student);
        
        // 更新班级学生数量
        List<Student> classStudents = studentMapper.selectByClassId(student.getClassId());
        classMapper.updateStudentCount(student.getClassId(), classStudents.size());
    }

    @Override
    @Transactional
    public void update(Student student) {
        // 检查学生是否存在
        Student existingStudent = studentMapper.selectById(student.getId());
        if (existingStudent == null) {
            throw new RuntimeException("学生不存在");
        }
        
        // 如果修改了班级，检查新班级是否存在
        if (!existingStudent.getClassId().equals(student.getClassId())) {
            Class clazz = classMapper.selectById(student.getClassId());
            if (clazz == null) {
                throw new RuntimeException("班级不存在");
            }
        }
        
        // 如果修改了学号，检查新学号是否已存在
        if (!existingStudent.getStudentNo().equals(student.getStudentNo())) {
            Student studentWithSameNo = studentMapper.selectByStudentNo(student.getStudentNo());
            if (studentWithSameNo != null) {
                throw new RuntimeException("学号已存在");
            }
        }
        
        // 更新学生信息
        studentMapper.update(student);
        
        // 如果修改了班级，更新两个班级的学生数量
        if (!existingStudent.getClassId().equals(student.getClassId())) {
            List<Student> oldClassStudents = studentMapper.selectByClassId(existingStudent.getClassId());
            List<Student> newClassStudents = studentMapper.selectByClassId(student.getClassId());
            classMapper.updateStudentCount(existingStudent.getClassId(), oldClassStudents.size());
            classMapper.updateStudentCount(student.getClassId(), newClassStudents.size());
        }
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new RuntimeException("请选择要删除的学生");
        }
        
        // 获取要删除的学生信息，用于更新班级学生数量
        Map<Long, Long> classIdMap = new HashMap<>();
        for (Long id : ids) {
            Student student = studentMapper.selectById(id);
            if (student != null) {
                classIdMap.put(id, student.getClassId());
            }
        }
        
        // 删除学生
        studentMapper.batchDelete(ids);
        
        // 更新相关班级的学生数量
        for (Long classId : classIdMap.values()) {
            List<Student> classStudents = studentMapper.selectByClassId(classId);
            classMapper.updateStudentCount(classId, classStudents.size());
        }
    }

    @Override
    @Transactional
    public Map<String, Object> importExcel(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        List<String> successList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            
            // 获取所有班级信息
            List<Class> classList = classMapper.selectList(null, null, null, 0, Integer.MAX_VALUE);
            Map<String, Long> classNameToIdMap = classList.stream()
                .collect(Collectors.toMap(Class::getClassName, Class::getId));
            
            // 从第二行开始读取数据（跳过表头）
            for (int i = 1; i < totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                try {
                    String studentNo = getCellValueAsString(row.getCell(0));
                    String name = getCellValueAsString(row.getCell(1));
                    String className = getCellValueAsString(row.getCell(2));
                    String gender = getCellValueAsString(row.getCell(3));
                    String phone = getCellValueAsString(row.getCell(4));
                    String email = getCellValueAsString(row.getCell(5));
                    
                    // 验证必填字段
                    if (studentNo == null || name == null || className == null || gender == null) {
                        errorList.add(String.format("第%d行：学号、姓名、班级、性别为必填项", i + 1));
                        continue;
                    }
                    
                    // 验证学号格式
                    if (!studentNo.matches("\\d{10}")) {
                        errorList.add(String.format("第%d行：学号必须为10位数字", i + 1));
                        continue;
                    }
                    
                    // 验证手机号格式
                    if (phone != null && !phone.matches("^1[3-9]\\d{9}$")) {
                        errorList.add(String.format("第%d行：手机号格式不正确", i + 1));
                        continue;
                    }
                    
                    // 验证邮箱格式
                    if (email != null && !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                        errorList.add(String.format("第%d行：邮箱格式不正确", i + 1));
                        continue;
                    }
                    
                    // 检查班级是否存在
                    Long classId = classNameToIdMap.get(className);
                    if (classId == null) {
                        errorList.add(String.format("第%d行：班级'%s'不存在", i + 1, className));
                        continue;
                    }
                    
                    // 检查学号是否已存在
                    Student existingStudent = studentMapper.selectByStudentNo(studentNo);
                    if (existingStudent != null) {
                        errorList.add(String.format("第%d行：学号'%s'已存在", i + 1, studentNo));
                        continue;
                    }
                    
                    // 创建学生对象
                    Student student = new Student();
                    student.setStudentNo(studentNo);
                    student.setName(name);
                    student.setClassId(classId);
                    student.setGender(gender);
                    student.setPhone(phone);
                    student.setEmail(email);
                    
                    // 保存学生信息
                    studentMapper.insert(student);
                    successList.add(String.format("第%d行：%s-%s", i + 1, studentNo, name));
                    
                } catch (Exception e) {
                    errorList.add(String.format("第%d行：%s", i + 1, e.getMessage()));
                }
            }
            
            // 更新班级学生数量
            for (Class clazz : classList) {
                List<Student> classStudents = studentMapper.selectByClassId(clazz.getId());
                classMapper.updateStudentCount(clazz.getId(), classStudents.size());
            }
            
        } catch (Exception e) {
            throw new RuntimeException("导入Excel失败：" + e.getMessage());
        }
        
        result.put("success", successList);
        result.put("error", errorList);
        return result;
    }

    @Override
    public byte[] exportExcel() {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("学生信息");
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"学号", "姓名", "班级", "性别", "联系电话", "邮箱"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }
            // 填充数据
            List<Student> studentList = getList(null, null, null, 1, Integer.MAX_VALUE);
            for (int i = 0; i < studentList.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Student student = studentList.get(i);
                row.createCell(0).setCellValue(student.getStudentNo());
                row.createCell(1).setCellValue(student.getName());
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

    private List<Class> getAllClasses() {
        return classMapper.selectList(null, null, null, 0, Integer.MAX_VALUE);
    }
} 