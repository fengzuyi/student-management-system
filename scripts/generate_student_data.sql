-- 创建存储过程来处理数据生成
DELIMITER //

DROP PROCEDURE IF EXISTS generate_student_data//
CREATE PROCEDURE generate_student_data()
BEGIN
    -- 声明变量
    DECLARE surname_count INT;
    DECLARE name_count INT;
    DECLARE course_count INT;
    DECLARE temp_count INT;
    DECLARE class_count INT;
    
    -- 验证班级数据
    SELECT COUNT(*) INTO class_count FROM class;
    IF class_count = 0 THEN
        -- 插入班级数据
        INSERT INTO class (id, class_name, grade, major) VALUES 
        (1, '计算机2101', 2021, '计算机科学与技术'),
        (2, '计算机2102', 2021, '计算机科学与技术'),
        (3, '软件2101', 2021, '软件工程'),
        (4, '软件2102', 2023, '软件工程'),
        (7, '计算机2003', 2022, '计算机科学与技术');
    END IF;

    -- 先删除成绩表中的相关数据
    DELETE FROM grade;

    -- 然后删除学生数据
    DELETE FROM student;

    -- 创建课程表（如果不存在）
    DROP TABLE IF EXISTS temp_courses;
    CREATE TABLE temp_courses (
        id INT PRIMARY KEY,
        course_code VARCHAR(10),
        course_name VARCHAR(50)
    );

    -- 插入课程数据
    INSERT INTO temp_courses (id, course_code, course_name) VALUES 
    (1, 'CS101', '计算机导论'),
    (2, 'CS102', '程序设计基础'),
    (3, 'CS201', '数据结构'),
    (4, 'CS202', '数据库原理'),
    (5, 'CS203', '操作系统');

    -- 创建姓氏表
    DROP TABLE IF EXISTS surname_pool;
    CREATE TABLE surname_pool (
        id INT AUTO_INCREMENT PRIMARY KEY,
        surname VARCHAR(10)
    );

    -- 创建名字表
    DROP TABLE IF EXISTS name_pool;
    CREATE TABLE name_pool (
        id INT AUTO_INCREMENT PRIMARY KEY,
        name VARCHAR(10)
    );

    -- 插入姓氏数据
    INSERT INTO surname_pool (surname) VALUES 
    ('张'),('王'),('李'),('赵'),('刘'),('陈'),('杨'),('黄'),('周'),('吴'),
    ('郑'),('孙'),('马'),('朱'),('胡'),('林'),('郭'),('何'),('高'),('罗'),
    ('梁'),('宋'),('唐'),('许'),('邓'),('冯'),('韩'),('曹'),('曾'),('彭'),
    ('萧'),('蔡'),('潘'),('田'),('董'),('袁'),('于'),('余'),('叶'),('蒋'),
    ('杜'),('苏'),('魏'),('程'),('吕'),('丁'),('沈'),('任'),('姚'),('卢');

    -- 插入名字数据
    INSERT INTO name_pool (name) VALUES 
    ('伟'),('芳'),('娜'),('秀英'),('敏'),('静'),('丽'),('强'),('磊'),('军'),
    ('洋'),('勇'),('艳'),('杰'),('娟'),('涛'),('明'),('超'),('秀兰'),('霞'),
    ('平'),('刚'),('桂英'),('玉兰'),('萍'),('健'),('建华'),('建国'),('建军'),('建平'),
    ('建明'),('建强'),('建文'),('建武'),('建新'),('建忠'),('建中'),('建民'),('建平'),('建安'),
    ('建华'),('建国'),('建军'),('建平'),('建明'),('建强'),('建文'),('建武'),('建新'),('建忠');

    -- 验证数据是否正确插入
    SELECT COUNT(*) INTO surname_count FROM surname_pool;
    SELECT COUNT(*) INTO name_count FROM name_pool;
    SELECT COUNT(*) INTO course_count FROM temp_courses;
    SELECT COUNT(*) INTO class_count FROM class;

    -- 如果数据没有正确插入，则报错
    IF surname_count = 0 OR name_count = 0 OR course_count = 0 OR class_count = 0 THEN
        SELECT CONCAT('数据插入失败：姓氏数量=', surname_count, 
                     ', 名字数量=', name_count,
                     ', 课程数量=', course_count,
                     ', 班级数量=', class_count) AS error;
        SIGNAL SQLSTATE '45000';
    END IF;

    -- 创建临时学生数据表
    DROP TABLE IF EXISTS temp_students;
    CREATE TABLE temp_students (
        id INT AUTO_INCREMENT PRIMARY KEY,
        student_no VARCHAR(10),
        name VARCHAR(50),
        class_id INT,
        gender VARCHAR(10),
        phone VARCHAR(20),
        email VARCHAR(100),
        create_time DATETIME,
        update_time DATETIME
    );

    -- 创建临时班级年级表
    DROP TABLE IF EXISTS temp_class_grades;
    CREATE TABLE temp_class_grades AS
    SELECT id, grade FROM class;

    -- 生成临时学生数据
    INSERT INTO temp_students (student_no, name, class_id, gender, phone, email, create_time, update_time)
    WITH RECURSIVE numbers AS (
        SELECT 1 as n
        UNION ALL
        SELECT n + 1 FROM numbers WHERE n < 500
    ),
    valid_classes AS (
        SELECT id FROM class
    ),
    student_base AS (
        SELECT 
            n as student_num,
            (SELECT id FROM valid_classes ORDER BY RAND() LIMIT 1) as selected_class_id
        FROM numbers
    )
    SELECT 
        CONCAT(
            (SELECT grade FROM temp_class_grades WHERE id = sb.selected_class_id),
            LPAD(FLOOR(1 + RAND() * 2), 2, '0'),
            LPAD(sb.student_num, 3, '0')
        ) as student_no,
        CONCAT(
            (SELECT surname FROM surname_pool ORDER BY RAND() LIMIT 1),
            (SELECT name FROM name_pool ORDER BY RAND() LIMIT 1)
        ) as name,
        sb.selected_class_id as class_id,
        IF(RAND() > 0.5, '男', '女') as gender,
        CONCAT(
            ELT(FLOOR(1 + RAND() * 3), '130', '138', '139'),
            LPAD(FLOOR(RAND() * 100000000), 8, '0')
        ) as phone,
        CONCAT(
            LOWER(
                CONCAT(
                    (SELECT surname FROM surname_pool ORDER BY RAND() LIMIT 1),
                    (SELECT name FROM name_pool ORDER BY RAND() LIMIT 1)
                )
            ),
            FLOOR(100 + RAND() * 900),
            ELT(FLOOR(1 + RAND() * 3), '@example.com', '@qq.com', '@163.com')
        ) as email,
        NOW() as create_time,
        NOW() as update_time
    FROM student_base sb;

    -- 验证临时表数据
    SELECT COUNT(*) INTO temp_count FROM temp_students;
    IF temp_count = 0 THEN
        SELECT '临时表数据生成失败' AS error;
        SIGNAL SQLSTATE '45000';
    END IF;

    -- 将临时学生数据插入到正式表
    INSERT INTO student (student_no, name, class_id, gender, phone, email, create_time, update_time)
    SELECT student_no, name, class_id, gender, phone, email, create_time, update_time
    FROM temp_students;

    -- 生成成绩数据
    INSERT INTO grade (student_id, course_id, score, semester, create_time, update_time)
    SELECT 
        s.id as student_id,
        c.id as course_id,
        -- 生成60-100之间的随机成绩，但大部分集中在70-90之间
        CASE 
            WHEN RAND() < 0.7 THEN FLOOR(70 + RAND() * 21)  -- 70%的概率在70-90之间
            WHEN RAND() < 0.9 THEN FLOOR(60 + RAND() * 10)  -- 20%的概率在60-70之间
            ELSE FLOOR(90 + RAND() * 11)                    -- 10%的概率在90-100之间
        END as score,
        CASE 
            WHEN c.id <= 2 THEN '2021-2022-1'  -- CS101和CS102在第一学期
            WHEN c.id <= 4 THEN '2021-2022-2'  -- CS201和CS202在第二学期
            ELSE '2022-2023-1'                 -- CS203在第三学期
        END as semester,
        NOW() as create_time,
        NOW() as update_time
    FROM student s
    CROSS JOIN temp_courses c;

    -- 删除所有临时表
    DROP TABLE IF EXISTS temp_students;
    DROP TABLE IF EXISTS surname_pool;
    DROP TABLE IF EXISTS name_pool;
    DROP TABLE IF EXISTS temp_courses;
    DROP TABLE IF EXISTS temp_class_grades;

    -- 更新班级学生人数
    UPDATE class c 
    SET student_count = (SELECT COUNT(*) FROM student s WHERE s.class_id = c.id);

    -- 输出成功信息
    SELECT '数据生成成功！' AS message;
    SELECT 
        (SELECT COUNT(*) FROM student) AS student_count,
        (SELECT COUNT(*) FROM grade) AS grade_count;
    SELECT 
        c.class_name,
        c.grade,
        c.major,
        c.student_count
    FROM class c
    ORDER BY c.id;
END //

DELIMITER ;

-- 执行存储过程
CALL generate_student_data(); 