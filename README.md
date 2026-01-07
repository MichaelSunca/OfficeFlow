# OfficeFlow - Enterprise Asset Management System

## 📝 Project Overview

**OfficeFlow** is a robust, full-stack enterprise resource management solution designed to streamline the tracking of corporate assets (hardware, software licenses, and office equipment).

As a software engineer with **over 10 years of cross-platform experience** (Android, iOS, .NET, and Java), I developed this project to demonstrate my proficiency in modernizing legacy workflows using the latest **Spring Boot 3** and **Vue 3** ecosystem. This project emphasizes clean architecture, secure authentication, and production-ready coding standards.

---

## 🚀 Key Features

* **RBAC Security:** Role-Based Access Control implemented via Spring Security and JWT for stateless, secure authentication.
* **Asset Lifecycle Tracking:** Complete CRUD management for corporate assets with advanced server-side pagination and filtering.
* **Approval Workflow:** A request-response system allowing employees to apply for equipment with real-time status tracking.
* **Enterprise Reporting:** Built-in functionality for **Excel/CSV data export**, a critical requirement for Japanese administrative compliance.
* **Modern Responsive UI:** A high-performance dashboard built with Vue 3, Vite, and Element Plus, optimized for both desktop and mobile views.

---

## 🛠 Tech Stack

### Backend (Java Ecosystem)

* **Runtime:** Java 21 (Utilizing modern features like Records and Pattern Matching)
* **Framework:** Spring Boot 3.4
* **Security:** Spring Security & JSON Web Token (JWT)
* **Persistence:** MyBatis-Plus (Optimized for complex SQL performance)
* **API Documentation:** SpringDoc OpenAPI / Swagger UI
* **Database:** MySQL 8.0

### Frontend (Modern Web)

* **Framework:** Vue 3 (Composition API / Script Setup)
* **Build Tool:** Vite (Next-generation frontend tooling)
* **State Management:** Pinia (Modular and type-safe)
* **UI Components:** Element Plus
* **HTTP Client:** Axios with centralized interceptors

---

## 📂 Project Structure

```text
.
├── backend-spring-boot/  # RESTful API Server (Spring Boot 3)
├── frontend-vue3/        # Single Page Application (Vue 3 + Vite)
├── sql/                  # Database schema and initialization scripts
├── docker-compose.yml    # Container orchestration for easy deployment
└── README.md             # Project documentation

```

---

## 🏃 Getting Started

### Prerequisites

* **JDK 21**
* **Node.js 20+**
* **MySQL 8.0**

### 1. Database Setup

To initialize the database, please execute the script in `/sql/init_db.sql`.

This script will:
* Create the `office_flow` database with the correct encoding (`utf8mb4`).
* Set up the `sys_user` table structure.
* Seed initial administrative data (Default credentials included in the script).

### 2. Backend Installation

```bash
cd backend-spring-boot
# Update application.yml with your database credentials
mvn clean install
mvn spring-boot:run

```

### 3. Frontend Installation

```bash
cd frontend-vue3
npm install
npm run dev

```

---

## 💡 Engineering Excellence

1. **Architecture:** Strict separation of concerns (Controller, Service, Mapper layers) ensuring high maintainability and testability.
2. **Scalability:** Implementation of standardized API response formats and global exception handling for a professional developer experience (DX).
3. **Cross-Platform Mindset:** Leveraging my extensive Android/iOS background to design mobile-friendly APIs and responsive frontend interfaces.

---

## 📫 Contact

**SUN**

* **Role:** Senior Software Engineer (10+ Years Experience)
* **Based in:** Tokyo, Japan


---


## commit type message
| **前缀 (Type)** | **适用场景**      |                        |
| ------------- | ------------- |--------------------------------|
| **feat**      | 新功能 (Feature) | 完成了登录页面、接入了新 SDK               |
| **fix**       | 修补 Bug        | 修复了 403 报错、修复了内存泄漏             |
| **docs**      | 文档变更          | 修改了 README、API 接口文档            |
| **style**     | 格式变动（不影响代码逻辑） | 修改变量命名、删掉多余空格、格式化代码            |
| **refactor**  | 重构            | 代码逻辑优化，既不是改 Bug 也不是加功能         |
| **chore**     | 构建过程或辅助工具的变动  | 修改 `pom.xml` 依赖、修改 `.gitignore` |
| **perf**      | 性能优化          | 优化了数据库查询速度                     |