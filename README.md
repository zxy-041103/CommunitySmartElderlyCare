# 社区智慧养老监护管理平台

## 项目简介

社区智慧养老监护管理平台是一个基于Spring Boot + Vue.js的全栈Web应用，旨在为社区老年人提供全方位的健康监护、生活服务和紧急救助服务。

## 技术栈

### 后端技术
- **框架**: Spring Boot 2.7.18
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.3.1
- **安全**: JWT Token认证
- **工具库**: Hutool、Lombok
- **Java版本**: 1.8

### 前端技术
- **框架**: Vue 3.3.4 + Vue Router 4.2.4
- **状态管理**: Pinia 2.1.6
- **UI组件**: Element Plus 2.3.14
- **构建工具**: Vite 4.4.9
- **图表库**: ECharts 5.4.3
- **HTTP客户端**: Axios 1.5.0

## 项目结构

```
CommunitySmartElderlyClare/
├── backend/          # 后端Spring Boot项目
│   ├── src/         # 源代码
│   ├── sql/         # 数据库脚本
│   └── pom.xml      # Maven依赖配置
└── frontend/        # 前端Vue项目
    ├── src/         # 源代码
    │   ├── views/   # 页面组件
    │   ├── api/     # API接口
    │   ├── store/   # 状态管理
    │   └── router/  # 路由配置
    └── package.json # 依赖配置
```

## 核心功能模块

### 1. 用户管理
- 多角色用户系统（管理员、护工、社区工作人员、普通用户）
- 用户注册、登录、权限控制
- 个人信息管理

### 2. 老人档案管理
- 老人基本信息管理
- 健康状况记录
- 紧急联系人信息
- 病史和过敏史记录

### 3. 健康监护
- 健康数据监测（心率、血压、血糖、体温、血氧）
- 健康预警系统
- 实时数据记录和图表展示

### 4. 紧急救助
- 紧急求助功能
- 跌倒检测和突发疾病报警
- 快速响应机制
- 救助记录管理

### 5. 养老服务
- 服务类别管理
- 服务项目预约
- 护工分配管理
- 服务评价系统

### 6. 社区活动
- 活动发布和管理
- 在线报名系统
- 活动状态跟踪

### 7. 公告管理
- 政策通知发布
- 健康知识推送
- 社区信息公告

### 8. 意见反馈
- 用户意见收集
- 问题反馈处理
- 满意度调查

## 数据库设计

项目包含13个核心数据表：
- 用户表（sys_user）
- 老人档案表（elderly_profile）
- 健康数据表（health_data）
- 健康预警表（health_alert）
- 紧急救助记录表（emergency_record）
- 服务类别表（service_category）
- 服务项目表（service_item）
- 服务预约表（service_appointment）
- 护工分配表（caregiver_assignment）
- 公告表（announcement）
- 活动表（activity）
- 活动报名表（activity_registration）
- 意见反馈表（feedback）

## 快速开始

### 环境要求
- JDK 1.8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

### 后端部署

1. 创建数据库
```sql
CREATE DATABASE elder_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

2. 导入数据库脚本
```bash
cd backend/sql
mysql -u root -p elder_care < init.sql
```

3. 配置数据库连接
修改 `application.yml` 中的数据库连接信息

4. 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

### 前端部署

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

3. 构建生产版本
```bash
npm run build
```

## 访问地址

- 前端应用: http://localhost:3000
- 后端API: http://localhost:8080

## 角色权限说明

### 管理员（ADMIN）
- 系统用户管理
- 所有数据查看和操作权限
- 系统配置管理

### 护工（CAREGIVER）
- 负责老人的健康监护
- 服务预约处理
- 紧急救助响应

### 社区工作人员（COMMUNITY）
- 社区活动管理
- 公告发布
- 服务预约处理

### 普通用户（USER）
- 个人信息管理
- 健康数据查看
- 服务预约和活动报名

