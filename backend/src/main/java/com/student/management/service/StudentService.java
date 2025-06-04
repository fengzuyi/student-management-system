package com.student.management.service;

import com.student.management.entity.Student;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface StudentService {
    List<Student> getList(String studentNo, String name, Long classId, Integer pageNum, Integer pageSize);
    int getCount(String studentNo, String name, Long classId);
    Student getById(Long id);
    Student getByStudentNo(String studentNo);
    void add(Student student);
    void update(Student student);
    void delete(List<Long> ids);
    Map<String, Object> importExcel(MultipartFile file);
    byte[] exportExcel();
    byte[] generateTemplate();
} 