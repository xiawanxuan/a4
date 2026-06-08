# 科研论文文献元数据管理与引文分析系统

## 项目简介

本系统是一个科研论文文献元数据管理与引文分析全栈应用，提供论文元数据管理、作者与机构关联、引文网络构建、被引次数统计、核心作者与机构分析等功能。

## 技术栈

### 前端
- Vue 3 + TypeScript
- Element Plus (UI组件库)
- Pinia (状态管理)
- Vue Router (路由管理)
- ECharts (数据可视化)
- Axios (HTTP客户端)
- Vite (构建工具)

### 后端
- Spring Boot 3.2.x
- Spring Data JPA
- MySQL 8.0+
- Lombok
- Maven

## 项目结构

```
.
├── backend/                 # 后端项目
│   ├── src/main/java/com/research/
│   │   ├── controller/      # 控制器层
│   │   ├── service/         # 服务层
│   │   ├── repository/      # 数据访问层
│   │   ├── entity/          # 实体类
│   │   ├── dto/             # 数据传输对象
│   │   ├── common/          # 公共类
│   │   ├── config/          # 配置类
│   │   └── exception/       # 异常处理
│   └── pom.xml
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── views/           # 页面组件
│   │   ├── api/             # API接口
│   │   ├── stores/          # Pinia状态管理
│   │   ├── router/          # 路由配置
│   │   ├── types/           # TypeScript类型定义
│   │   └── App.vue
│   └── package.json
└── database/                # 数据库脚本
    ├── schema.sql           # 数据库建表脚本
    └── seed_data.sql        # 测试数据
```

## 功能模块

### 1. 论文元数据管理
- 论文的增删改查
- 按标题、关键词、作者、年份等多条件检索
- 分页查询与排序
- 批量导入（支持JSON、CSV格式）

### 2. 作者与机构管理
- 作者信息管理（姓名、ORCID、H指数等）
- 机构信息管理（名称、国家、类型等）
- 作者-机构关联关系维护
- 论文-作者/机构多对多关联

### 3. 引文网络构建
- 引用关系管理
- 论文引文网络图可视化（ECharts力导向图）
- 引用/被引论文查询
- 引文深度可配置

### 4. 统计分析功能
- 数据概览（论文数、作者数、机构数、期刊数）
- 核心作者分析（按发文量、被引次数、H指数排序）
- 核心机构分析
- 发文趋势分析
- 期刊分布统计
- 研究领域关键词共现分析
- 高被引论文排行

## 本地部署

### 环境要求
- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 步骤一：数据库准备

1. 创建数据库并执行建表脚本：

```bash
mysql -u root -p < database/schema.sql
```

2. （可选）导入测试数据：

```bash
mysql -u root -p research_db < database/seed_data.sql
```

### 步骤二：启动后端服务

1. 配置数据库连接：

编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/research_db?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

2. 编译并启动：

```bash
cd backend
mvn clean package -DskipTests
java -jar target/research-analysis-1.0.0.jar
```

或使用Maven直接运行：

```bash
cd backend
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 步骤三：启动前端服务

1. 安装依赖：

```bash
cd frontend
npm install
```

2. 启动开发服务器：

```bash
npm run dev
```

前端服务默认运行在 `http://localhost:5173`

3. （可选）构建生产版本：

```bash
npm run build
```

构建产物将输出到 `dist` 目录。

## API 接口说明

### 论文管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/papers | 分页查询论文列表 |
| GET | /api/papers/{id} | 获取论文详情 |
| POST | /api/papers | 创建论文 |
| PUT | /api/papers/{id} | 更新论文 |
| DELETE | /api/papers/{id} | 删除论文 |
| GET | /api/papers/{id}/citations | 获取论文的引用文献 |
| GET | /api/papers/{id}/cited-by | 获取引用该论文的文献 |
| GET | /api/papers/{id}/citation-network | 获取论文引文网络 |
| GET | /api/papers/statistics | 获取论文统计信息 |

### 作者管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/authors | 分页查询作者列表 |
| GET | /api/authors/{id} | 获取作者详情 |
| POST | /api/authors | 创建作者 |
| PUT | /api/authors/{id} | 更新作者 |
| DELETE | /api/authors/{id} | 删除作者 |
| GET | /api/authors/{id}/papers | 获取作者的论文列表 |

### 机构管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/institutions | 分页查询机构列表 |
| GET | /api/institutions/{id} | 获取机构详情 |
| POST | /api/institutions | 创建机构 |
| PUT | /api/institutions/{id} | 更新机构 |
| DELETE | /api/institutions/{id} | 删除机构 |
| GET | /api/institutions/{id}/papers | 获取机构的论文列表 |
| GET | /api/institutions/{id}/authors | 获取机构的作者列表 |

### 引文管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/citations/network | 获取引文网络 |
| GET | /api/citations/citing/{paperId} | 获取论文的参考文献 |
| GET | /api/citations/cited/{paperId} | 获取引用该论文的文献 |
| POST | /api/citations | 添加引用关系 |
| DELETE | /api/citations/{id} | 删除引用关系 |

### 数据分析接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/analysis/overview | 获取数据概览 |
| GET | /api/analysis/core-authors | 获取核心作者分析 |
| GET | /api/analysis/core-institutions | 获取核心机构分析 |
| GET | /api/analysis/publication-trend | 获取发文趋势 |
| GET | /api/analysis/top-cited-papers | 获取高被引论文 |
| GET | /api/analysis/journal-distribution | 获取期刊分布 |
| GET | /api/analysis/keyword-cooccurrence | 获取关键词共现分析 |

### 批量导入接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/import/json | JSON格式批量导入论文 |
| POST | /api/import/csv | CSV格式批量导入论文 |
| POST | /api/import/citations | 批量导入引用关系 |
| GET | /api/import/template | 下载导入模板 |

## 数据导入说明

### JSON格式导入

请求体示例：

```json
[
  {
    "title": "论文标题",
    "authors": ["作者1", "作者2"],
    "abstractText": "摘要内容",
    "keywords": "关键词1,关键词2,关键词3",
    "doi": "10.1234/example",
    "journalName": "期刊名称",
    "publicationYear": 2024,
    "totalCitations": 0,
    "institutions": ["机构1", "机构2"]
  }
]
```

### CSV格式导入

CSV文件首行需包含以下字段：
`title,authors,abstractText,keywords,doi,journalName,publicationYear,volume,issue,pages,totalCitations`

多个作者使用 `;` 分隔。

## 注意事项

1. 数据库表名和字段名采用下划线命名法
2. 前端已配置 `/api` 代理到后端 `http://localhost:8080`
3. 所有 API 响应均为统一格式：`{ code: 200, message: "success", data: ... }`
4. 分页参数统一使用 `pageNum`（页码，从1开始）和 `pageSize`（每页条数）

## 开发说明

### 后端开发规范
- 采用分层架构：Controller → Service → Repository → Entity
- 使用 DTO 进行数据传输，Entity 仅用于数据库映射
- 统一异常处理和响应格式
- 使用 Lombok 简化实体类代码

### 前端开发规范
- 使用 Composition API 和 `<script setup>` 语法
- 使用 TypeScript 类型约束
- 使用 Pinia 进行状态管理
- 组件化开发，按页面和功能模块组织
