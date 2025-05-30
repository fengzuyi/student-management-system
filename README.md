# 学生管理系统

这是一个基于前后端分离架构的学生管理系统，采用现代化的技术栈开发。

## 技术栈

### 前端

- Vue 3.3.4
- TypeScript
- Vite 4.4.5
- Element Plus 2.3.9
- Vue Router 4.2.4
- Axios 1.4.0

### 后端

- Spring Boot 3.2.3
- Java 17
- Spring Security
- MyBatis 3.0.3
- MySQL 8.0.33
- JWT 认证
- Apache POI (Excel 处理)

## 项目结构

```
├── frontend/                # 前端项目目录
│   ├── src/                # 源代码
│   ├── public/             # 静态资源
│   ├── package.json        # 项目配置和依赖
│   └── vite.config.ts      # Vite配置
│
├── backend/                # 后端项目目录
│   ├── src/               # 源代码
│   └── pom.xml            # Maven配置和依赖
│
└── scripts/               # 项目sql脚本
```

## 环境要求

- Node.js 16+
- Java 17+
- MySQL 8.0+
- Maven 3.6+

## 快速开始

### 后端启动

1. 确保已安装 Java 17 和 Maven
2. 配置 MySQL 数据库
3. 进入 backend 目录
4. 执行以下命令：

```bash
mvn clean install
mvn spring-boot:run
```

### 前端启动

1. 确保已安装 Node.js
2. 进入 frontend 目录
3. 执行以下命令：

```bash
npm install
npm run dev
```

## 配置说明

### 后端配置

后端配置文件位于 `backend/src/main/resources/application.yml`，主要配置项包括：

- 数据库连接信息
- 服务器端口
- JWT 配置
- 跨域设置

### 前端配置

前端配置文件位于 `frontend/.env`，主要配置项包括：

- API 接口地址
- 环境变量

## 功能特性

- 用户认证与授权
- 学生信息管理
- 课程管理
- 成绩管理
- Excel 导入导出
- 数据可视化

## 开发指南

1. 代码规范遵循各自技术栈的最佳实践
2. 前端使用 TypeScript 进行开发
3. 后端遵循 RESTful API 设计规范
4. 使用 Git 进行版本控制

## 部署说明

1. 前端构建：

```bash
cd frontend
npm run build
```

2. 后端构建：

```bash
cd backend
mvn clean package
```

3. 部署生成的构建文件到相应的服务器

## 注意事项

- 确保数据库配置正确
- 注意保护敏感配置信息
- 定期备份数据库
- 遵循安全最佳实践

## 贡献指南

1. Fork 项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

MIT License
