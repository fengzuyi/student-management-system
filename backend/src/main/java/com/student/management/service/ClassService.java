package com.student.management.service;

import com.student.management.entity.Class;
import java.util.List;

public interface ClassService {
    List<Class> getList();
    Class getById(Long id);
    void add(Class clazz);
    void update(Class clazz);
    void delete(Long id);
    void updateStudentCount(Long id, Integer count);
    byte[] exportExcel();
    void batchDelete(List<Long> ids);
} 