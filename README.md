# OfficeFlow - 企业资产管理系统

<div align="center">

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5+-green.svg)
![Vue](https://img.shields.io/badge/Vue-3.5+-4FC08D.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0+-4479A1.svg)

**一款轻量级、响应式的企业资产管理系统，采用前后端分离架构**

[功能特性](#功能特性) • [技术栈](#技术栈) • [快速开始](#快速开始) • [项目结构](#项目结构)

</div>

---

## 📖 项目简介

OfficeFlow 是一款专为中小型企业设计的资产管理系统，旨在简化企业的资产管理、行政审批与组织架构维护。系统采用现代化的前后端分离架构，核心目标是**高效、安全、无状态**。

> 💡 本项目主要用于学习 Spring Boot 和 Vue 3 技术栈，通过实际项目开发来掌握前后端分离架构的设计与实现。

### 核心功能

- ✅ **用户认证与授权**：基于 JWT 的无状态认证体系，支持角色权限控制
- ✅ **资产管理**：完整的资产台账管理，支持资产的增删改查、领用、退库等操作
- ✅ **审批流程**：资产领用审批流程，管理员可集中处理待审批事项
- ✅ **流转记录**：完整的资产流转历史记录，支持追溯和审计
- ✅ **状态追踪**：实时查看资产状态（闲置、领用中、维修中、报废）

---

## 🛠 技术栈

### 后端技术

- **Java 21** - 现代 Java 语言特性
- **Spring Boot 3.5+** - 企业级应用框架
- **Spring Security** - 安全认证框架
- **JWT (JSON Web Token)** - 无状态身份认证
- **MyBatis-Plus 3.5+** - 持久层框架
- **MySQL 8.0+** - 关系型数据库
- **Maven** - 项目构建工具

### 前端技术

- **Vue 3.5+** - 渐进式 JavaScript 框架
- **TypeScript** - 类型安全的 JavaScript 超集
- **Vite 7+** - 下一代前端构建工具
- **Element Plus** - Vue 3 组件库
- **Vue Router 4** - 官方路由管理器
- **Pinia** - Vue 状态管理库
- **Axios** - HTTP 客户端

### 开发规范

- **RESTful API** - 标准化接口设计
- **JWT Stateless Auth** - 无状态认证
- **UTF-8 MB4** - 支持多语言（中文、日文、英文）

---

## ✨ 功能特性

### 1. 身份认证与安全

- [x] 基于 JWT 的无状态登录体系
- [x] BCrypt 密码加密存储
- [x] 基于角色的权限控制（ADMIN / USER）
- [x] 全局异常处理机制
- [x] 请求拦截与认证过滤

### 2. 资产管理

- [x] 资产台账管理（增删改查）
- [x] 资产分类管理
- [x] 资产状态追踪（闲置、领用中、维修、报废）
- [x] 资产领用申请
- [x] 资产退库操作
- [x] 资产搜索与筛选
- [x] 资产二维码生成
- [x] 资产流转时间线展示

### 3. 审批流程

- [x] 资产领用审批
- [x] 待审批列表查询
- [x] 审批结果处理（通过/驳回）
- [x] 审批备注记录

### 4. 用户管理

- [x] 用户信息查询
- [x] 用户角色管理
- [x] 用户状态管理

### 5. 数据审计

- [x] 资产流转记录完整追踪
- [x] 操作日志记录
- [x] 逻辑删除机制
- [x] 创建/更新时间自动记录

---

## 📁 项目结构

```
OfficeFlow/
├── backend-spring-boot/          # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/officeflow/backend/
│   │   │   │       ├── BackendApplication.java    # 启动类
│   │   │   │       ├── common/                    # 通用组件
│   │   │   │       ├── config/                    # 配置类
│   │   │   │       ├── controller/                # 控制器层
│   │   │   │       ├── dto/                       # 数据传输对象
│   │   │   │       ├── entity/                    # 实体类
│   │   │   │       ├── mapper/                    # MyBatis Mapper
│   │   │   │       ├── service/                   # 服务层
│   │   │   │       ├── security/                  # 安全相关
│   │   │   │       ├── utils/                     # 工具类
│   │   │   │       └── vo/                        # 视图对象
│   │   │   └── resources/
│   │   │       ├── application.yaml               # 应用配置
│   │   │       └── mapper/                        # MyBatis XML 映射文件
│   │   └── test/                                  # 测试代码
│   └── pom.xml                                    # Maven 依赖配置
│
├── frontend-vue3/                 # Vue 3 前端项目
│   ├── src/
│   │   ├── api/                   # API 接口定义
│   │   ├── assets/                # 静态资源
│   │   ├── components/            # 公共组件
│   │   ├── layout/                # 布局组件
│   │   ├── router/                # 路由配置
│   │   ├── store/                 # 状态管理
│   │   ├── utils/                 # 工具函数
│   │   ├── views/                 # 页面视图
│   │   │   ├── assets/            # 资产管理页面
│   │   │   ├── login/             # 登录页面
│   │   │   └── user/              # 用户管理页面
│   │   ├── App.vue                # 根组件
│   │   └── main.ts                # 入口文件
│   ├── package.json               # 依赖配置
│   └── vite.config.ts             # Vite 配置
│
├── docs/                          # 项目文档
│   ├── DB_DESIGN.md               # 数据库设计文档
│   └── FEATURES.md                # 功能特性文档
│
├── sql/                           # 数据库脚本
│   └── init_db.sql                # 数据库初始化脚本
│
└── api-test/                      # API 测试文件
    ├── auth.http                  # 认证接口测试
    ├── asset.http                 # 资产接口测试
    ├── record.http                # 记录接口测试
    └── user.http                  # 用户接口测试
```

---

## 🚀 快速开始

### 环境要求

- **JDK 21+**
- **Maven 3.6+**
- **Node.js 18+**
- **MySQL 8.0+**
- **npm 或 yarn**

### 1. 克隆项目

```bash
git clone https://github.com/your-username/OfficeFlow.git
cd OfficeFlow
```

### 2. 数据库初始化

1. 创建 MySQL 数据库：

```sql
CREATE DATABASE IF NOT EXISTS `office_flow`
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：

```bash
mysql -u root -p office_flow < sql/init_db.sql
```

或者直接在 MySQL 客户端中执行 `sql/init_db.sql` 文件。

### 3. 后端配置与启动

1. 进入后端目录：

```bash
cd backend-spring-boot
```

2. 配置数据库连接：

复制 `src/main/resources/application.yml.example` 为 `application.yaml`，并修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/office_flow?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

3. 启动后端服务：

```bash
mvn spring-boot:run
```

后端服务默认运行在 `http://localhost:8080`

### 4. 前端配置与启动

1. 进入前端目录：

```bash
cd frontend-vue3
```

2. 安装依赖：

```bash
npm install
```

3. 配置 API 地址（如需要）：

编辑 `src/utils/request.ts`，修改 `baseURL` 为后端服务地址。

4. 启动开发服务器：

```bash
npm run dev
```

前端服务默认运行在 `http://localhost:5173`

### 5. 访问系统

- 前端地址：http://localhost:5173
- 后端 API：http://localhost:8080/api

**默认账号：**
- 管理员：`admin` / `123456`
- 普通用户：`user01` / `123456`

---

## 📝 API 文档

### 认证接口

- `POST /api/auth/login` - 用户登录

### 资产管理接口

- `GET /api/assets/list` - 获取资产列表（分页）
- `POST /api/assets/add` - 新增资产
- `POST /api/assets/update` - 更新资产
- `POST /api/assets/delete` - 删除资产
- `POST /api/assets/claim` - 领用资产
- `POST /api/assets/return` - 退库资产
- `GET /api/assets/records/{assetId}` - 获取资产流转记录

### 审批接口

- `GET /api/assets/audit/list` - 获取待审批列表
- `POST /api/assets/audit/handle` - 处理审批（需 ADMIN 权限）

### 用户接口

- `GET /api/user/info` - 获取当前用户信息
- `GET /api/user/search` - 搜索用户

详细的 API 测试文件位于 `api-test/` 目录下。

---

## 🗄 数据库设计

系统主要包含以下数据表：

- **sys_user** - 用户表
- **sys_role** - 角色表
- **bus_asset** - 资产表
- **bus_record** - 资产流转记录表

详细的数据库设计文档请参考 [docs/DB_DESIGN.md](docs/DB_DESIGN.md)

---

## 🔧 开发说明

### 后端开发

1. **代码规范**：
   - 遵循阿里巴巴 Java 开发规范
   - 使用 Lombok 简化代码
   - 统一使用中文注释

2. **包结构说明**：
   - `controller` - 处理 HTTP 请求
   - `service` - 业务逻辑层
   - `mapper` - 数据访问层
   - `entity` - 实体类
   - `dto` - 数据传输对象
   - `vo` - 视图对象

3. **安全配置**：
   - JWT Token 有效期：7 天
   - 密码加密：BCrypt
   - 接口权限：基于 Spring Security

### 前端开发

1. **技术选型**：
   - 使用 Composition API
   - TypeScript 类型约束
   - Element Plus 组件库

2. **目录说明**：
   - `api/` - API 接口封装
   - `views/` - 页面组件
   - `components/` - 公共组件
   - `store/` - 状态管理
   - `utils/` - 工具函数

---

## 📋 开发计划

- [x] **Phase 1**: 搭建安全底座与 JWT 认证 ✅
- [x] **Phase 2**: 资产管理模块 CRUD 与数据库表设计 ✅
- [x] **Phase 3**: 资产领用、退库与审批流程 ✅
- [x] **Phase 4**: Vue 3 前端界面开发 ✅
- [ ] **Phase 5**: 组织架构与部门管理
- [ ] **Phase 6**: 报表导出功能（Excel/PDF）
- [ ] **Phase 7**: 多语言支持（i18n）
- [ ] **Phase 8**: 移动端适配

---

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

---

## 🙏 致谢

感谢以下开源项目：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis-Plus](https://baomidou.com/)

---

<div align="center">

**如果这个项目对你有帮助，请给一个 ⭐ Star！**

</div>
