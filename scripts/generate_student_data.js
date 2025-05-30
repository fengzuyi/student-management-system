const fs = require('fs');
const path = require('path');

// 生成随机数
function getRandomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}

// 生成随机姓名
function generateName() {
  const surnames = ['张', '王', '李', '赵', '刘', '陈', '杨', '黄', '周', '吴', '郑', '孙', '马', '朱', '胡', '林', '郭', '何', '高', '罗'];
  const names = ['伟', '芳', '娜', '秀英', '敏', '静', '丽', '强', '磊', '军', '洋', '勇', '艳', '杰', '娟', '涛', '明', '超', '秀兰', '霞'];
  return surnames[getRandomInt(0, surnames.length - 1)] + names[getRandomInt(0, names.length - 1)];
}

// 生成随机学号
function generateStudentNo() {
  const year = '2021';  // 固定为2021级
  const major = getRandomInt(1, 2).toString().padStart(2, '0');  // 1-2表示专业
  const sequence = getRandomInt(1, 999).toString().padStart(3, '0');
  return `${year}${major}${sequence}`;
}

// 生成随机班级ID（根据数据库中的班级表）
function generateClassId() {
  return getRandomInt(1, 5);  // 数据库中有5个班级
}

// 生成随机性别
function generateGender() {
  return Math.random() > 0.5 ? '男' : '女';
}

// 生成随机手机号
function generatePhone() {
  const prefixes = ['130', '131', '132', '133', '134', '135', '136', '137', '138', '139',
                   '150', '151', '152', '153', '155', '156', '157', '158', '159',
                   '180', '181', '182', '183', '184', '185', '186', '187', '188', '189'];
  const prefix = prefixes[getRandomInt(0, prefixes.length - 1)];
  const suffix = getRandomInt(10000000, 99999999);
  return `${prefix}${suffix}`;
}

// 生成随机邮箱
function generateEmail(name) {
  const domains = ['@example.com', '@qq.com', '@163.com'];
  const domain = domains[getRandomInt(0, domains.length - 1)];
  return `${name.toLowerCase()}${getRandomInt(100, 999)}${domain}`;
}

// 生成学生数据
function generateStudents(count) {
  const students = [];
  const usedStudentNos = new Set();  // 用于确保学号唯一

  for (let i = 0; i < count; i++) {
    let studentNo;
    do {
      studentNo = generateStudentNo();
    } while (usedStudentNos.has(studentNo));
    usedStudentNos.add(studentNo);

    const name = generateName();
    const student = {
      student_no: studentNo,
      name: name,
      class_id: generateClassId(),
      gender: generateGender(),
      phone: generatePhone(),
      email: generateEmail(name),
      create_time: new Date().toISOString().slice(0, 19).replace('T', ' '),
      update_time: new Date().toISOString().slice(0, 19).replace('T', ' ')
    };
    students.push(student);
  }
  return students;
}

// 生成SQL插入语句
function generateSQL(students) {
  const sqlStatements = [];
  
  // 添加学生数据
  sqlStatements.push('-- 插入学生数据');
  sqlStatements.push('INSERT INTO student (student_no, name, class_id, gender, phone, email, create_time, update_time) VALUES');
  
  const values = students.map(student => {
    return `('${student.student_no}', '${student.name}', ${student.class_id}, '${student.gender}', '${student.phone}', '${student.email}', '${student.create_time}', '${student.update_time}')`;
  });
  
  sqlStatements.push(values.join(',\n') + ';');
  
  // 更新班级学生人数
  sqlStatements.push('\n-- 更新班级学生人数');
  sqlStatements.push('UPDATE class SET student_count = (SELECT COUNT(*) FROM student WHERE class_id = 1) WHERE id = 1;');
  sqlStatements.push('UPDATE class SET student_count = (SELECT COUNT(*) FROM student WHERE class_id = 2) WHERE id = 2;');
  sqlStatements.push('UPDATE class SET student_count = (SELECT COUNT(*) FROM student WHERE class_id = 3) WHERE id = 3;');
  sqlStatements.push('UPDATE class SET student_count = (SELECT COUNT(*) FROM student WHERE class_id = 4) WHERE id = 4;');
  sqlStatements.push('UPDATE class SET student_count = (SELECT COUNT(*) FROM student WHERE class_id = 5) WHERE id = 5;');
  
  return sqlStatements.join('\n');
}

// 生成数据并保存到文件
const students = generateStudents(500);
const outputPath = path.join(__dirname, '../data/students.sql');

// 确保目录存在
if (!fs.existsSync(path.dirname(outputPath))) {
  fs.mkdirSync(path.dirname(outputPath), { recursive: true });
}

// 写入SQL文件
fs.writeFileSync(outputPath, generateSQL(students), 'utf8');

console.log(`成功生成 ${students.length} 条学生数据，已保存到 ${outputPath}`); 