package com.student.management.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.management.entity.OperationLog;
import com.student.management.entity.Student;
import com.student.management.mapper.StudentMapper;
import com.student.management.service.OperationLogService;
import com.student.management.util.SecurityUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
public class OperationLogAspect {

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentMapper studentMapper;

    // 修改切点表达式，排除OperationLogController
    @Pointcut("execution(* com.student.management.controller.*.*(..)) && " +
              "!execution(* com.student.management.controller.OperationLogController.*(..)) && " +
              "(@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
              "@annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void operationLogPointcut() {}

    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 获取当前用户ID
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) {
            return point.proceed();
        }

        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getMethod().getName();
        String className = point.getTarget().getClass().getSimpleName();
        
        // 获取操作类型
        String operationType = getOperationType(className, methodName);
        
        // 获取操作内容
        String operationContent = getOperationContent(point, className, methodName);

        // 执行原方法
        Object result = point.proceed();

        // 记录日志
        OperationLog log = new OperationLog();
        log.setUserId(userId);
        log.setOperationType(operationType);
        log.setOperationContent(operationContent);
        log.setCreateTime(LocalDateTime.now());
        operationLogService.addLog(log);

        return result;
    }

    private String getOperationType(String className, String methodName) {
        String entityName = getEntityName(className);
        
        if (methodName.startsWith("add") || methodName.startsWith("create")) {
            return "新增" + entityName;
        } else if (methodName.startsWith("update") || methodName.startsWith("modify")) {
            return "修改" + entityName;
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "删除" + entityName;
        } else if (methodName.startsWith("import")) {
            return "导入" + entityName + "数据";
        } else if (methodName.startsWith("export")) {
            return "导出" + entityName + "数据";
        } else if (methodName.contains("Grade")) {
            return "成绩管理";
        }
        return "其他操作";
    }

    private String getEntityName(String className) {
        // 从Controller类名中提取实体名称
        String name = className.replace("Controller", "");
        switch (name) {
            case "Student": return "学生";
            case "Class": return "班级";
            case "Course": return "课程";
            case "Grade": return "成绩";
            case "OperationLog": return "操作日志";
            default: return name;
        }
    }

    private String getOperationContent(ProceedingJoinPoint point, String className, String methodName) {
        Object[] args = point.getArgs();
        StringBuilder content = new StringBuilder();
        String entityName = getEntityName(className);
        
        try {
            if (methodName.startsWith("add") || methodName.startsWith("create")) {
                content.append("新增").append(entityName);
                if (args.length > 0 && args[0] != null) {
                    content.append("：").append(formatObject(args[0]));
                }
            } else if (methodName.startsWith("update") || methodName.startsWith("modify")) {
                content.append("修改").append(entityName);
                if (args.length > 1 && args[1] != null) {
                    content.append("：").append(formatObject(args[1]));
                } else if (args.length > 0 && args[0] != null) {
                    content.append("：").append(formatObject(args[0]));
                }
            } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
                content.append("删除").append(entityName);
                if (args.length > 0) {
                    if (args[0] instanceof Map) {
                        Map<String, Object> params = (Map<String, Object>) args[0];
                        if (params.containsKey("ids")) {
                            List<Long> ids = (List<Long>) params.get("ids");
                            if ("学生".equals(entityName)) {
                                // 获取学生学号列表
                                List<String> studentNos = getStudentNos(ids);
                                content.append("：").append(studentNos);
                            } else {
                                content.append("：").append(ids);
                            }
                        } else {
                            content.append("：").append(formatObject(args[0]));
                        }
                    } else if (args[0] instanceof List) {
                        List<Long> ids = (List<Long>) args[0];
                        if ("学生".equals(entityName)) {
                            // 获取学生学号列表
                            List<String> studentNos = getStudentNos(ids);
                            content.append("：").append(studentNos);
                        } else {
                            content.append("：").append(ids);
                        }
                    } else {
                        content.append("：").append(formatObject(args[0]));
                    }
                }
            } else if (methodName.startsWith("import")) {
                content.append("导入").append(entityName).append("数据");
            } else if (methodName.startsWith("export")) {
                content.append("导出").append(entityName).append("数据");
            } else if (methodName.contains("Grade")) {
                if (methodName.contains("add")) {
                    content.append("录入成绩：").append(formatObject(args[0]));
                } else if (methodName.contains("update")) {
                    content.append("修改成绩：").append(formatObject(args[1]));
                } else if (methodName.contains("delete")) {
                    content.append("删除成绩：").append(formatObject(args[0]));
                }
            } else {
                content.append("执行").append(methodName).append("操作");
                if (args.length > 0) {
                    content.append("：").append(formatObject(args[0]));
                }
            }
        } catch (Exception e) {
            content.append("执行").append(methodName).append("操作");
            if (args.length > 0) {
                content.append("：").append(args[0]);
            }
        }
        
        return content.toString();
    }

    private List<String> getStudentNos(List<Long> ids) {
        List<String> studentNos = new ArrayList<>();
        for (Long id : ids) {
            Student student = studentMapper.selectById(id);
            if (student != null) {
                studentNos.add(student.getStudentNo());
            }
        }
        return studentNos;
    }

    private String formatObject(Object obj) {
        try {
            if (obj == null) {
                return "";
            }
            if (obj instanceof String) {
                return (String) obj;
            }
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return obj.toString();
        }
    }
} 