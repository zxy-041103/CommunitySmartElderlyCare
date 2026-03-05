# 社区智慧养老监护管理平台 - 项目架构说明

## 项目概述
本项目是一个基于SpringBoot和Vue 3的社区智慧养老监护管理平台，采用前后端分离架构，实现了RBAC权限控制、JWT令牌验证、适老化模块等核心功能。

## 技术栈

### 后端技术栈
- SpringBoot 2.7.x
- MyBatis-Plus
- MySQL 8.0
- Redis
- Spring Security
- JWT (Java JWT)
- AES加密
- 协同过滤算法

### 前端技术栈
- Vue 3
- Element Plus
- Vue Router
- Pinia
- Axios
- ECharts
- 百度语音识别API（预留接口）

## 项目结构

### 后端目录结构
```
backend/
├── src/main/java/com/community/elderly/
│   ├── ElderlyCareApplication.java          # 主启动类
│   ├── entity/                               # 实体层
│   │   ├── User.java                        # 用户实体
│   │   ├── Role.java                        # 角色实体
│   │   ├── Permission.java                  # 权限实体
│   │   ├── Menu.java                        # 菜单实体
│   │   ├── RolePermission.java              # 角色权限关联
│   │   └── RoleMenu.java                    # 角色菜单关联
│   ├── controller/                          # 控制层
│   ├── service/                              # 业务层
│   ├── mapper/                               # 数据访问层
│   ├── config/                               # 配置类
│   └── utils/                                # 工具类
│       ├── JwtUtil.java                     # JWT工具类
│       └── AesUtil.java                     # AES加密工具类
└── src/main/resources/
    ├── application.yml                      # 应用配置
    └── mapper/                               # MyBatis映射文件
```

### 前端目录结构
```
frontend/
├── src/
│   ├── main.js                              # 入口文件
│   ├── App.vue                              # 根组件
│   ├── api/                                 # API接口
│   │   └── user.js                          # 用户相关API
│   ├── assets/                              # 静态资源
│   │   └── styles/
│   │       └── index.scss                   # 全局样式
│   ├── components/                          # 组件层
│   │   └── elderly/                         # 适老化组件
│   │       ├── VoiceRecognition.vue         # 语音识别组件
│   │       ├── FontResizer.vue              # 字体放大组件
│   │       └── ElderlyToolbar.vue           # 适老化工具栏
│   ├── router/                              # 路由层
│   │   └── index.js                         # 路由配置
│   ├── store/                               # 状态管理层
│   │   └── user.js                          # 用户状态管理
│   ├── utils/                               # 工具类
│   │   └── request.js                       # Axios封装
│   └── views/                               # 视图层
│       ├── Login.vue                        # 登录页
│       ├── Layout.vue                       # 布局页
│       ├── Dashboard.vue                    # 首页
│       ├── elderly/                         # 老人管理
│       ├── health/                          # 健康监测
│       ├── emergency/                       # 紧急求助
│       ├── activity/                        # 活动管理
│       └── system/                          # 系统管理
├── package.json                             # 前端依赖配置
└── vite.config.js                           # Vite配置
```

## 核心功能

### 1. RBAC权限模型
- 支持四种角色：老人、家属、护工、管理员
- 实现了用户、角色、权限、菜单的实体类
- 支持角色与权限、角色与菜单的关联关系

### 2. JWT令牌验证
- [JwtUtil.java](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/backend/src/main/java/com/community/elderly/utils/JwtUtil.java) 实现了JWT令牌的生成、解析和验证
- 支持令牌过期检查
- 支持从令牌中提取用户信息（用户ID、用户名、角色）

### 3. AES加密
- [AesUtil.java](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/backend/src/main/java/com/community/elderly/utils/AesUtil.java) 实现了AES加密解密
- 支持密码加密存储
- 使用CBC模式和PKCS5Padding填充

### 4. 适老化模块
- [VoiceRecognition.vue](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/src/components/elderly/VoiceRecognition.vue) 语音识别组件
  - 支持中文语音识别
  - 支持连续识别和单次识别
  - 提供开始/停止控制
- [FontResizer.vue](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/src/components/elderly/FontResizer.vue) 字体放大组件
  - 支持字体大小调节（12px-24px）
  - 支持一键重置
- [ElderlyToolbar.vue](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/src/components/elderly/ElderlyToolbar.vue) 适老化工具栏
  - 集成语音识别和字体放大功能
  - 支持高对比度模式切换

### 5. 前端路由拦截
- [router/index.js](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/src/router/index.js) 实现了基于角色的路由拦截
- 未登录用户自动跳转到登录页
- 已登录用户访问登录页自动跳转到首页
- 根据用户角色控制路由访问权限

### 6. 用户状态管理
- [store/user.js](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/src/store/user.js) 使用Pinia管理用户状态
- 管理用户令牌、用户信息、角色等信息
- 提供登录、获取用户信息、登出等方法

### 7. Axios请求封装
- [utils/request.js](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/src/utils/request.js) 封装了Axios请求
- 自动添加JWT令牌到请求头
- 统一处理响应和错误
- 支持请求/响应拦截器

### 8. 跨域配置
- [vite.config.js](file:///d:/CommunitySmartElderlyCareMonitoringandManagementPlatform/frontend/vite.config.js) 配置了跨域代理
- 前端请求/api路径自动代理到后端8080端口

## 数据库表设计

### 系统用户表 (sys_user)
- id: 主键
- username: 用户名
- password: 密码（AES加密）
- name: 姓名
- phone: 电话
- email: 邮箱
- avatar: 头像
- gender: 性别
- age: 年龄
- address: 地址
- roleId: 角色ID
- status: 状态
- createTime: 创建时间
- updateTime: 更新时间
- deleted: 逻辑删除标记

### 系统角色表 (sys_role)
- id: 主键
- roleName: 角色名称
- roleCode: 角色编码
- description: 描述
- status: 状态
- sort: 排序
- createTime: 创建时间
- updateTime: 更新时间
- deleted: 逻辑删除标记

### 系统权限表 (sys_permission)
- id: 主键
- permissionName: 权限名称
- permissionCode: 权限编码
- type: 类型
- parentId: 父权限ID
- path: 路径
- component: 组件
- icon: 图标
- sort: 排序
- status: 状态
- createTime: 创建时间
- updateTime: 更新时间
- deleted: 逻辑删除标记

### 系统菜单表 (sys_menu)
- id: 主键
- menuName: 菜单名称
- parentId: 父菜单ID
- path: 路径
- component: 组件
- icon: 图标
- sort: 排序
- visible: 是否可见
- status: 状态
- createTime: 创建时间
- updateTime: 更新时间
- deleted: 逻辑删除标记

### 角色权限关联表 (sys_role_permission)
- id: 主键
- roleId: 角色ID
- permissionId: 权限ID
- createTime: 创建时间
- deleted: 逻辑删除标记

### 角色菜单关联表 (sys_role_menu)
- id: 主键
- roleId: 角色ID
- menuId: 菜单ID
- createTime: 创建时间
- deleted: 逻辑删除标记

## 配置说明

### 后端配置 (application.yml)
- 服务器端口: 8080
- 数据库: MySQL 8.0
- Redis: 本地6379端口
- JWT密钥和过期时间配置
- MyBatis-Plus配置

### 前端配置 (vite.config.js)
- 开发服务器端口: 3000
- 跨域代理配置
- 自动打开浏览器

## 下一步开发建议

1. 完善后端业务逻辑层和控制器层
2. 实现用户登录、注册接口
3. 实现健康监测、紧急求助等核心业务功能
4. 集成百度语音识别API
5. 实现协同过滤算法推荐功能
6. 完善前端页面和交互
7. 添加单元测试和集成测试
8. 部署上线

## 注意事项

1. 确保MySQL和Redis服务已启动
2. 前端开发需要先安装依赖：npm install
3. 后端开发需要配置好数据库连接信息
4. JWT密钥在生产环境中应使用更安全的值
5. AES加密密钥应妥善保管
