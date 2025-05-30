-- 修改课程表学分字段类型为 double
ALTER TABLE course MODIFY COLUMN credits DOUBLE NOT NULL COMMENT '学分'; 