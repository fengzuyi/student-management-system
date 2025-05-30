-- 测试数据生成脚本
-- 生成时间：2025年

-- 删除并重建数据库
DROP DATABASE IF EXISTS student_management;
CREATE DATABASE student_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE student_management;

-- 创建表结构
CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    real_name VARCHAR(50),
    role VARCHAR(20) NOT NULL COMMENT 'ADMIN/TEACHER',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_name VARCHAR(50) NOT NULL,
    grade VARCHAR(20) NOT NULL,
    major VARCHAR(50) NOT NULL,
    student_count INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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

CREATE TABLE IF NOT EXISTS course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_code VARCHAR(20) NOT NULL UNIQUE,
    course_name VARCHAR(100) NOT NULL,
    credits DOUBLE NOT NULL,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

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

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    operation_content TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES sys_user(id)
);

-- 清理现有数据（注意删除顺序要符合外键约束）
DELETE FROM operation_log;
DELETE FROM grade;
DELETE FROM student;
DELETE FROM course;
DELETE FROM class;
DELETE FROM sys_user;

-- 重置自增ID
ALTER TABLE operation_log AUTO_INCREMENT = 1;
ALTER TABLE grade AUTO_INCREMENT = 1;
ALTER TABLE student AUTO_INCREMENT = 1;
ALTER TABLE course AUTO_INCREMENT = 1;
ALTER TABLE class AUTO_INCREMENT = 1;
ALTER TABLE sys_user AUTO_INCREMENT = 1; 

USE student_management;

-- 生成100条课程数据
INSERT INTO course (course_code, course_name, credits) VALUES
('CS101', '计算机导论', 3),
('CS102', '程序设计基础', 4),
('CS201', '数据结构', 4),
('CS202', '数据库原理', 3),
('CS203', '操作系统', 4),
('CS204', '计算机网络', 4),
('CS205', '软件工程', 3),
('CS206', '人工智能导论', 3),
('CS207', '机器学习基础', 4),
('CS208', '深度学习', 4),
('CS209', '计算机图形学', 3),
('CS210', '编译原理', 4),
('CS211', '算法设计与分析', 4),
('CS212', '计算机组成原理', 4),
('CS213', '数字电路', 3),
('CS214', '模拟电路', 3),
('CS215', '信号处理', 4),
('CS216', '嵌入式系统', 3),
('CS217', '移动应用开发', 3),
('CS218', 'Web开发技术', 3),
('CS219', '云计算技术', 4),
('CS220', '大数据处理', 4),
('CS221', '信息安全', 3),
('CS222', '密码学基础', 3),
('CS223', '软件测试', 3),
('CS224', '软件项目管理', 3),
('CS225', '人机交互', 3),
('CS226', '计算机视觉', 4),
('CS227', '自然语言处理', 4),
('CS228', '分布式系统', 4),
('CS229', '并行计算', 4),
('CS230', '高性能计算', 4),
('CS231', '量子计算导论', 3),
('CS232', '区块链技术', 3),
('CS233', '物联网技术', 3),
('CS234', '5G通信技术', 3),
('CS235', '虚拟现实技术', 3),
('CS236', '增强现实技术', 3),
('CS237', '游戏开发', 4),
('CS238', '多媒体技术', 3),
('CS239', '数字图像处理', 3),
('CS240', '模式识别', 4),
('CS241', '智能机器人', 4),
('CS242', '计算机仿真', 3),
('CS243', '软件架构设计', 4),
('CS244', '微服务架构', 3),
('CS245', '容器技术', 3),
('CS246', 'DevOps实践', 3),
('CS247', '敏捷开发', 3),
('CS248', '软件质量保证', 3),
('CS249', '软件维护', 3),
('CS250', '软件重构', 3),
('CS251', '代码优化', 3),
('CS252', '软件安全', 3),
('CS253', '网络编程', 4),
('CS254', '操作系统内核', 4),
('CS255', '数据库优化', 3),
('CS256', '数据挖掘', 4),
('CS257', '知识图谱', 3),
('CS258', '推荐系统', 3),
('CS259', '搜索引擎技术', 3),
('CS260', '社交网络分析', 3),
('CS261', '计算机动画', 3),
('CS262', '数字音频处理', 3),
('CS263', '计算机音乐', 3),
('CS264', '计算机辅助设计', 3),
('CS265', '计算机辅助制造', 3),
('CS266', '计算机辅助工程', 3),
('CS267', '计算机辅助教学', 3),
('CS268', '教育技术', 3),
('CS269', '远程教育技术', 3),
('CS270', '在线学习系统', 3),
('CS271', '智能教育系统', 3),
('CS272', '教育数据分析', 3),
('CS273', '教育评估系统', 3),
('CS274', '教育管理系统', 3),
('CS275', '教育资源共享', 3),
('CS276', '教育信息安全', 3),
('CS277', '教育云平台', 3),
('CS278', '教育大数据', 3),
('CS279', '教育人工智能', 3),
('CS280', '教育区块链', 3),
('CS281', '教育物联网', 3),
('CS282', '教育5G应用', 3),
('CS283', '教育VR/AR', 3),
('CS284', '教育游戏化', 3),
('CS285', '教育多媒体', 3),
('CS286', '教育模式识别', 3),
('CS287', '教育机器人', 3),
('CS288', '教育仿真', 3),
('CS289', '教育架构设计', 3),
('CS290', '教育微服务', 3),
('CS291', '教育容器化', 3),
('CS292', '教育DevOps', 3),
('CS293', '教育敏捷开发', 3),
('CS294', '教育质量保证', 3),
('CS295', '教育系统维护', 3),
('CS296', '教育系统重构', 3),
('CS297', '教育代码优化', 3),
('CS298', '教育系统安全', 3),
('CS299', '教育网络编程', 3),
('CS300', '教育操作系统', 3);

-- 生成100条用户数据
INSERT INTO sys_user (username, password, real_name, role) VALUES
('admin', '123456', '系统管理员', 'ADMIN'),
('teacher1', '123456', '张老师', 'TEACHER'),
('teacher2', '123456', '李老师', 'TEACHER'),
('teacher3', '123456', '王老师', 'TEACHER'),
('teacher4', '123456', '赵老师', 'TEACHER'),
('teacher5', '123456', '钱老师', 'TEACHER'),
('teacher6', '123456', '孙老师', 'TEACHER'),
('teacher7', '123456', '周老师', 'TEACHER'),
('teacher8', '123456', '吴老师', 'TEACHER'),
('teacher9', '123456', '郑老师', 'TEACHER'),
('teacher10', '123456', '刘老师', 'TEACHER'),
('teacher11', '123456', '陈老师', 'TEACHER'),
('teacher12', '123456', '杨老师', 'TEACHER'),
('teacher13', '123456', '黄老师', 'TEACHER'),
('teacher14', '123456', '胡老师', 'TEACHER'),
('teacher15', '123456', '林老师', 'TEACHER'),
('teacher16', '123456', '梁老师', 'TEACHER'),
('teacher17', '123456', '谢老师', 'TEACHER'),
('teacher18', '123456', '宋老师', 'TEACHER'),
('teacher19', '123456', '唐老师', 'TEACHER'),
('teacher20', '123456', '许老师', 'TEACHER'),
('teacher21', '123456', '邓老师', 'TEACHER'),
('teacher22', '123456', '冯老师', 'TEACHER'),
('teacher23', '123456', '韩老师', 'TEACHER'),
('teacher24', '123456', '曹老师', 'TEACHER'),
('teacher25', '123456', '曾老师', 'TEACHER'),
('teacher26', '123456', '彭老师', 'TEACHER'),
('teacher27', '123456', '萧老师', 'TEACHER'),
('teacher28', '123456', '蔡老师', 'TEACHER'),
('teacher29', '123456', '潘老师', 'TEACHER'),
('teacher30', '123456', '田老师', 'TEACHER'),
('teacher31', '123456', '董老师', 'TEACHER'),
('teacher32', '123456', '袁老师', 'TEACHER'),
('teacher33', '123456', '于老师', 'TEACHER'),
('teacher34', '123456', '余老师', 'TEACHER'),
('teacher35', '123456', '叶老师', 'TEACHER'),
('teacher36', '123456', '程老师', 'TEACHER'),
('teacher37', '123456', '苏老师', 'TEACHER'),
('teacher38', '123456', '魏老师', 'TEACHER'),
('teacher39', '123456', '吕老师', 'TEACHER'),
('teacher40', '123456', '丁老师', 'TEACHER'),
('teacher41', '123456', '沈老师', 'TEACHER'),
('teacher42', '123456', '任老师', 'TEACHER'),
('teacher43', '123456', '姚老师', 'TEACHER'),
('teacher44', '123456', '卢老师', 'TEACHER'),
('teacher45', '123456', '傅老师', 'TEACHER'),
('teacher46', '123456', '钟老师', 'TEACHER'),
('teacher47', '123456', '姜老师', 'TEACHER'),
('teacher48', '123456', '崔老师', 'TEACHER'),
('teacher49', '123456', '谭老师', 'TEACHER'),
('teacher50', '123456', '廖老师', 'TEACHER'),
('teacher51', '123456', '范老师', 'TEACHER'),
('teacher52', '123456', '汪老师', 'TEACHER'),
('teacher53', '123456', '陆老师', 'TEACHER'),
('teacher54', '123456', '金老师', 'TEACHER'),
('teacher55', '123456', '石老师', 'TEACHER'),
('teacher56', '123456', '戴老师', 'TEACHER'),
('teacher57', '123456', '贾老师', 'TEACHER'),
('teacher58', '123456', '韦老师', 'TEACHER'),
('teacher59', '123456', '夏老师', 'TEACHER'),
('teacher60', '123456', '邱老师', 'TEACHER'),
('teacher61', '123456', '方老师', 'TEACHER'),
('teacher62', '123456', '侯老师', 'TEACHER'),
('teacher63', '123456', '邹老师', 'TEACHER'),
('teacher64', '123456', '熊老师', 'TEACHER'),
('teacher65', '123456', '孟老师', 'TEACHER'),
('teacher66', '123456', '秦老师', 'TEACHER'),
('teacher67', '123456', '白老师', 'TEACHER'),
('teacher68', '123456', '江老师', 'TEACHER'),
('teacher69', '123456', '阎老师', 'TEACHER'),
('teacher70', '123456', '薛老师', 'TEACHER'),
('teacher71', '123456', '尹老师', 'TEACHER'),
('teacher72', '123456', '段老师', 'TEACHER'),
('teacher73', '123456', '雷老师', 'TEACHER'),
('teacher74', '123456', '黎老师', 'TEACHER'),
('teacher75', '123456', '史老师', 'TEACHER'),
('teacher76', '123456', '陶老师', 'TEACHER'),
('teacher77', '123456', '贺老师', 'TEACHER'),
('teacher78', '123456', '毛老师', 'TEACHER'),
('teacher79', '123456', '郝老师', 'TEACHER'),
('teacher80', '123456', '顾老师', 'TEACHER'),
('teacher81', '123456', '龚老师', 'TEACHER'),
('teacher82', '123456', '邵老师', 'TEACHER'),
('teacher83', '123456', '万老师', 'TEACHER'),
('teacher84', '123456', '钱老师', 'TEACHER'),
('teacher85', '123456', '严老师', 'TEACHER'),
('teacher86', '123456', '赖老师', 'TEACHER'),
('teacher87', '123456', '武老师', 'TEACHER'),
('teacher88', '123456', '康老师', 'TEACHER'),
('teacher89', '123456', '贺老师', 'TEACHER'),
('teacher90', '123456', '文老师', 'TEACHER'),
('teacher91', '123456', '常老师', 'TEACHER'),
('teacher92', '123456', '温老师', 'TEACHER'),
('teacher93', '123456', '庄老师', 'TEACHER'),
('teacher94', '123456', '晏老师', 'TEACHER'),
('teacher95', '123456', '柴老师', 'TEACHER'),
('teacher96', '123456', '瞿老师', 'TEACHER'),
('teacher97', '123456', '阎老师', 'TEACHER'),
('teacher98', '123456', '连老师', 'TEACHER'),
('teacher99', '123456', '习老师', 'TEACHER'),
('teacher100', '123456', '齐老师', 'TEACHER');

-- 生成100条班级数据
INSERT INTO class (class_name, grade, major, student_count) VALUES
('计算机2101', '2021', '计算机科学与技术', 0),
('计算机2102', '2021', '计算机科学与技术', 0),
('计算机2103', '2021', '计算机科学与技术', 0),
('计算机2104', '2021', '计算机科学与技术', 0),
('计算机2105', '2021', '计算机科学与技术', 0),
('软件2101', '2021', '软件工程', 0),
('软件2102', '2021', '软件工程', 0),
('软件2103', '2021', '软件工程', 0),
('软件2104', '2021', '软件工程', 0),
('软件2105', '2021', '软件工程', 0),
('网络2101', '2021', '网络工程', 0),
('网络2102', '2021', '网络工程', 0),
('网络2103', '2021', '网络工程', 0),
('网络2104', '2021', '网络工程', 0),
('网络2105', '2021', '网络工程', 0),
('人工智能2101', '2021', '人工智能', 0),
('人工智能2102', '2021', '人工智能', 0),
('人工智能2103', '2021', '人工智能', 0),
('人工智能2104', '2021', '人工智能', 0),
('人工智能2105', '2021', '人工智能', 0),
('计算机2201', '2022', '计算机科学与技术', 0),
('计算机2202', '2022', '计算机科学与技术', 0),
('计算机2203', '2022', '计算机科学与技术', 0),
('计算机2204', '2022', '计算机科学与技术', 0),
('计算机2205', '2022', '计算机科学与技术', 0),
('软件2201', '2022', '软件工程', 0),
('软件2202', '2022', '软件工程', 0),
('软件2203', '2022', '软件工程', 0),
('软件2204', '2022', '软件工程', 0),
('软件2205', '2022', '软件工程', 0),
('网络2201', '2022', '网络工程', 0),
('网络2202', '2022', '网络工程', 0),
('网络2203', '2022', '网络工程', 0),
('网络2204', '2022', '网络工程', 0),
('网络2205', '2022', '网络工程', 0),
('人工智能2201', '2022', '人工智能', 0),
('人工智能2202', '2022', '人工智能', 0),
('人工智能2203', '2022', '人工智能', 0),
('人工智能2204', '2022', '人工智能', 0),
('人工智能2205', '2022', '人工智能', 0),
('计算机2301', '2023', '计算机科学与技术', 0),
('计算机2302', '2023', '计算机科学与技术', 0),
('计算机2303', '2023', '计算机科学与技术', 0),
('计算机2304', '2023', '计算机科学与技术', 0),
('计算机2305', '2023', '计算机科学与技术', 0),
('软件2301', '2023', '软件工程', 0),
('软件2302', '2023', '软件工程', 0),
('软件2303', '2023', '软件工程', 0),
('软件2304', '2023', '软件工程', 0),
('软件2305', '2023', '软件工程', 0),
('网络2301', '2023', '网络工程', 0),
('网络2302', '2023', '网络工程', 0),
('网络2303', '2023', '网络工程', 0),
('网络2304', '2023', '网络工程', 0),
('网络2305', '2023', '网络工程', 0),
('人工智能2301', '2023', '人工智能', 0),
('人工智能2302', '2023', '人工智能', 0),
('人工智能2303', '2023', '人工智能', 0),
('人工智能2304', '2023', '人工智能', 0),
('人工智能2305', '2023', '人工智能', 0),
('计算机2401', '2024', '计算机科学与技术', 0),
('计算机2402', '2024', '计算机科学与技术', 0),
('计算机2403', '2024', '计算机科学与技术', 0),
('计算机2404', '2024', '计算机科学与技术', 0),
('计算机2405', '2024', '计算机科学与技术', 0),
('软件2401', '2024', '软件工程', 0),
('软件2402', '2024', '软件工程', 0),
('软件2403', '2024', '软件工程', 0),
('软件2404', '2024', '软件工程', 0),
('软件2405', '2024', '软件工程', 0),
('网络2401', '2024', '网络工程', 0),
('网络2402', '2024', '网络工程', 0),
('网络2403', '2024', '网络工程', 0),
('网络2404', '2024', '网络工程', 0),
('网络2405', '2024', '网络工程', 0),
('人工智能2401', '2024', '人工智能', 0),
('人工智能2402', '2024', '人工智能', 0),
('人工智能2403', '2024', '人工智能', 0),
('人工智能2404', '2024', '人工智能', 0),
('人工智能2405', '2024', '人工智能', 0),
('计算机2501', '2025', '计算机科学与技术', 0),
('计算机2502', '2025', '计算机科学与技术', 0),
('计算机2503', '2025', '计算机科学与技术', 0),
('计算机2504', '2025', '计算机科学与技术', 0),
('计算机2505', '2025', '计算机科学与技术', 0),
('软件2501', '2025', '软件工程', 0),
('软件2502', '2025', '软件工程', 0),
('软件2503', '2025', '软件工程', 0),
('软件2504', '2025', '软件工程', 0),
('软件2505', '2025', '软件工程', 0),
('网络2501', '2025', '网络工程', 0),
('网络2502', '2025', '网络工程', 0),
('网络2503', '2025', '网络工程', 0),
('网络2504', '2025', '网络工程', 0),
('网络2505', '2025', '网络工程', 0),
('人工智能2501', '2025', '人工智能', 0),
('人工智能2502', '2025', '人工智能', 0),
('人工智能2503', '2025', '人工智能', 0),
('人工智能2504', '2025', '人工智能', 0),
('人工智能2505', '2025', '人工智能', 0); 

-- 生成学生数据（为每个班级生成30名学生）
INSERT INTO student (student_no, name, class_id, gender, phone, email) 
WITH RECURSIVE numbers AS (
    SELECT 1 as n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 30
),
class_data AS (
    SELECT id, grade FROM class
)
SELECT 
    CONCAT(SUBSTRING(c.grade, 3, 2), 
           LPAD(c.id, 2, '0'), 
           LPAD(n.n, 3, '0')) as student_no,
    CASE 
        WHEN n.n % 3 = 0 THEN CONCAT('张', n.n, '号')
        WHEN n.n % 3 = 1 THEN CONCAT('李', n.n, '号')
        ELSE CONCAT('王', n.n, '号')
    END as name,
    c.id as class_id,
    CASE WHEN n.n % 2 = 0 THEN '男' ELSE '女' END as gender,
    CONCAT('138', LPAD(c.id * 100 + n.n, 8, '0')) as phone,
    CONCAT('student', LPAD(c.id * 100 + n.n, 8, '0'), '@example.com') as email
FROM class_data c
CROSS JOIN numbers n;

-- 更新班级学生人数
UPDATE class c
SET student_count = (
    SELECT COUNT(*) 
    FROM student s 
    WHERE s.class_id = c.id
);

-- 生成成绩数据（为每个学生生成5-8门课程的成绩）
INSERT INTO grade (student_id, course_id, score, semester)
WITH RECURSIVE student_courses AS (
    SELECT 
        s.id as student_id,
        c.id as course_id,
        CONCAT(
            SUBSTRING(cl.grade, 1, 4), 
            '-', 
            SUBSTRING(cl.grade, 1, 4) + 1,
            '-',
            CASE 
                WHEN RAND() < 0.5 THEN '1'
                ELSE '2'
            END
        ) as semester
    FROM student s
    JOIN class cl ON s.class_id = cl.id
    JOIN course c ON 1=1
    WHERE RAND() < 0.3  -- 随机选择约30%的课程组合
)
SELECT 
    student_id,
    course_id,
    ROUND(60 + RAND() * 40, 1) as score,  -- 生成60-100之间的随机分数
    semester
FROM student_courses;

-- 生成操作日志
INSERT INTO operation_log (user_id, operation_type, operation_content)
SELECT 
    u.id as user_id,
    CASE 
        WHEN RAND() < 0.3 THEN 'ADD_STUDENT'
        WHEN RAND() < 0.6 THEN 'ADD_GRADE'
        ELSE 'UPDATE_GRADE'
    END as operation_type,
    CASE 
        WHEN RAND() < 0.3 THEN CONCAT('添加学生：', s.name)
        WHEN RAND() < 0.6 THEN CONCAT('录入成绩：', s.name, '-', c.course_name)
        ELSE CONCAT('修改成绩：', s.name, '-', c.course_name)
    END as operation_content
FROM sys_user u
CROSS JOIN (
    SELECT id, name FROM student ORDER BY RAND() LIMIT 100
) s
CROSS JOIN (
    SELECT id, course_name FROM course ORDER BY RAND() LIMIT 100
) c
WHERE RAND() < 0.1  -- 随机生成约10%的操作记录
LIMIT 1000;  -- 限制最多生成1000条操作日志 