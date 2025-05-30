-- 创建数据库
CREATE DATABASE IF NOT EXISTS student_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_management;

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    role VARCHAR(20) NOT NULL COMMENT 'ADMIN/TEACHER',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 班级表
CREATE TABLE IF NOT EXISTS class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(50) NOT NULL,
    grade VARCHAR(20) NOT NULL,
    major VARCHAR(50) NOT NULL,
    student_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 学生表
CREATE TABLE IF NOT EXISTS student (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_no VARCHAR(10) NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL,
    class_id BIGINT NOT NULL,
    gender VARCHAR(10),
    phone VARCHAR(20),
    email VARCHAR(100),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (class_id) REFERENCES class(id)
);

-- 课程表
CREATE TABLE IF NOT EXISTS course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credits INT NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 成绩表
CREATE TABLE IF NOT EXISTS grade (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    score DECIMAL(5,2) NOT NULL,
    semester VARCHAR(20) NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES student(id),
    FOREIGN KEY (course_id) REFERENCES course(id)
);

-- 操作日志表
CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_content TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
); 

-- 插入管理员和教师账号
INSERT INTO sys_user (username, password, real_name, role, create_time, update_time) VALUES
( 'admin', '123456', '系统管理员', 'ADMIN', '2025-05-28 21:15:34', '2025-05-28 21:15:34'),
('teacher1', '123456', '张老师', 'TEACHER', '2025-05-28 21:15:34', '2025-05-28 21:15:34'),
('teacher2', '123456', '李老师', 'TEACHER', '2025-05-28 21:15:34', '2025-05-28 21:15:34');


-- 插入班级数据
INSERT INTO class (class_name, grade, major, student_count) VALUES
('计算机2101', '2021', '计算机科学与技术', 0),
('计算机2102', '2021', '计算机科学与技术', 0),
('软件2101', '2021', '软件工程', 0),
('软件2102', '2021', '软件工程', 0),
('网络2101', '2021', '网络工程', 0);

-- 插入学生数据
INSERT INTO student (student_no, name, class_id, gender, phone, email) VALUES
('2021001001', '张三', 1, '男', '13800138001', 'zhangsan@example.com'),
('2021001002', '李四', 1, '男', '13800138002', 'lisi@example.com'),
('2021001003', '王五', 1, '女', '13800138003', 'wangwu@example.com'),
('2021001004', '赵六', 2, '男', '13800138004', 'zhaoliu@example.com'),
('2021001005', '钱七', 2, '女', '13800138005', 'qianqi@example.com'),
('2021001006', '孙八', 3, '男', '13800138006', 'sunba@example.com'),
('2021001007', '周九', 3, '女', '13800138007', 'zhoujiu@example.com'),
('2021001008', '吴十', 4, '男', '13800138008', 'wushi@example.com'),
('2021001009', '郑十一', 4, '女', '13800138009', 'zhengshiyi@example.com'),
('2021001010', '王十二', 5, '男', '13800138010', 'wangshier@example.com');

-- 更新班级学生人数
UPDATE class SET student_count = 3 WHERE id = 1;
UPDATE class SET student_count = 2 WHERE id = 2;
UPDATE class SET student_count = 2 WHERE id = 3;
UPDATE class SET student_count = 2 WHERE id = 4;
UPDATE class SET student_count = 1 WHERE id = 5;

-- 插入课程数据
INSERT INTO course (course_code, course_name, credits) VALUES
('CS101', '计算机导论', 3),
('CS102', '程序设计基础', 4),
('CS201', '数据结构', 4),
('CS202', '数据库原理', 3),
('CS203', '操作系统', 4);

-- 插入成绩数据
INSERT INTO grade (student_id, course_id, score, semester) VALUES
(1, 1, 85.5, '2021-2022-1'),
(1, 2, 90.0, '2021-2022-1'),
(2, 1, 78.5, '2021-2022-1'),
(2, 2, 88.0, '2021-2022-1'),
(3, 1, 92.0, '2021-2022-1'),
(3, 2, 85.5, '2021-2022-1'),
(4, 1, 76.0, '2021-2022-1'),
(4, 2, 82.5, '2021-2022-1'),
(5, 1, 88.5, '2021-2022-1'),
(5, 2, 90.0, '2021-2022-1');

-- 插入操作日志
INSERT INTO operation_log (user_id, operation_type, operation_content) VALUES
(1, 'ADD_STUDENT', '添加学生：张三'),
(1, 'ADD_STUDENT', '添加学生：李四'),
(2, 'ADD_GRADE', '录入成绩：张三-计算机导论'),
(2, 'ADD_GRADE', '录入成绩：李四-计算机导论'); 