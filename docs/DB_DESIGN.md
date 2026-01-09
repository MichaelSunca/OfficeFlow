# OfficeFlow 数据库设计文档 (v1.0)

## 1. 设计原则

- **命名规范**：系统表以 `sys_` 开头，业务表以 `bus_` 开头。
- **字符集**：使用 `utf8mb4` 以支持多语言（包括中文、日文、英文及 Emoji）。
- **通用字段**：核心表均包含 `create_time` 和 `update_time` 以便审计。
- **逻辑删除**：关键业务数据使用逻辑删除字段 `del_flag`，防止误删导致数据丢失。
- **索引优化**：为常用查询字段建立索引，提升查询性能。
- **外键关联**：通过 `user_id`、`role_id`、`asset_id` 等字段建立表间关联关系。

---

## 2. 数据库信息

- **数据库名称**：`office_flow`
- **字符集**：`utf8mb4`
- **排序规则**：`utf8mb4_unicode_ci`
- **存储引擎**：`InnoDB`
- **目标版本**：MySQL 8.0+

---

## 3. 表结构详细说明

### 3.1 系统角色表 (`sys_role`)

存储系统角色信息，用于权限控制。


| 字段名         | 类型          | 约束               | 默认值               | 备注                  |
| ----------- | ----------- | ---------------- | ----------------- | ------------------- |
| id          | BIGINT      | PK, AI           | -                 | 角色 ID（主键）           |
| role_name   | VARCHAR(50) | NOT NULL         | -                 | 角色名称（展示用，如：超级管理员）   |
| role_key    | VARCHAR(50) | UNIQUE, NOT NULL | -                 | 角色标识（代码判权用，如：ADMIN） |
| status      | TINYINT     | -                | 1                 | 角色状态：1=正常, 0=禁用     |
| create_time | DATETIME    | -                | CURRENT_TIMESTAMP | 创建时间                |


**索引说明**：

- 主键索引：`PRIMARY KEY (id)`
- 唯一索引：`UNIQUE (role_key)`

**初始数据**：

- `id=1, role_name='超级管理员', role_key='ADMIN'`
- `id=2, role_name='普通员工', role_key='USER'`

---

### 3.2 系统用户表 (`sys_user`)

存储员工账户、登录凭证及基本身份信息。


| 字段名         | 类型           | 约束               | 默认值                         | 备注                                   |
| ----------- | ------------ | ---------------- | --------------------------- | ------------------------------------ |
| id          | BIGINT       | PK, AI           | -                           | 主键 ID                                |
| username    | VARCHAR(50)  | UNIQUE, NOT NULL | -                           | 登录账号或工号                              |
| password    | VARCHAR(100) | NOT NULL         | -                           | BCrypt 加密密码                          |
| nickname    | VARCHAR(50)  | -                | NULL                        | 员工真实姓名                               |
| avatar      | VARCHAR(255) | -                | 默认头像URL                     | 用户头像                                 |
| email       | VARCHAR(100) | -                | NULL                        | 邮箱地址                                 |
| phone       | VARCHAR(20)  | -                | NULL                        | 联系电话                                 |
| role_id     | BIGINT       | NOT NULL         | 2                           | 角色ID（关联 sys_role.id）：1=ADMIN, 2=USER |
| status      | TINYINT      | -                | 1                           | 账号状态：1=正常, 0=禁用                      |
| del_flag    | TINYINT      | -                | 0                           | 逻辑删除：0=未删除, 1=已删除                    |
| create_time | DATETIME     | -                | CURRENT_TIMESTAMP           | 创建时间                                 |
| update_time | DATETIME     | -                | CURRENT_TIMESTAMP ON UPDATE | 更新时间                                 |


**索引说明**：

- 主键索引：`PRIMARY KEY (id)`
- 唯一索引：`UNIQUE INDEX uk_username (username)`
- 普通索引：`INDEX idx_role_id (role_id)`

**初始数据**：

- `username='admin', password='$2a$10$...', nickname='超级管理员', role_id=1`
- `username='user01', password='$2a$10$...', nickname='普通员工', role_id=2`
- 默认密码：`123456`（BCrypt 加密后存储）

---

### 3.3 资产台账表 (`bus_asset`)

存储公司所有固定资产的核心静态信息及当前状态。


| 字段名           | 类型            | 约束       | 默认值                         | 备注                           |
| ------------- | ------------- | -------- | --------------------------- | ---------------------------- |
| id            | BIGINT        | PK, AI   | -                           | 主键 ID                        |
| asset_name    | VARCHAR(100)  | NOT NULL | -                           | 资产名称（如：MacBook Pro）          |
| asset_sn      | VARCHAR(50)   | NOT NULL | -                           | 资产序列号/机身码（唯一标识）              |
| category      | VARCHAR(30)   | -        | 'General'                   | 分类：如 IT设备, 办公家具, 行政用品        |
| price         | DECIMAL(12,2) | -        | 0.00                        | 采购价格（支持多币种，保留2位小数）           |
| status        | TINYINT       | -        | 0                           | 资产状态：0=闲置, 1=领用中, 2=维修, 3=报废 |
| user_id       | BIGINT        | -        | NULL                        | 当前持有者/领用人 ID（关联 sys_user.id） |
| location      | VARCHAR(100)  | -        | NULL                        | 存放地点                         |
| purchase_date | DATE          | -        | NULL                        | 采购日期                         |
| create_by     | VARCHAR(50)   | -        | NULL                        | 创建者账号                        |
| create_time   | DATETIME      | -        | CURRENT_TIMESTAMP           | 创建时间                         |
| update_by     | VARCHAR(50)   | -        | NULL                        | 更新者账号                        |
| update_time   | DATETIME      | -        | CURRENT_TIMESTAMP ON UPDATE | 更新时间                         |
| del_flag      | TINYINT       | -        | 0                           | 逻辑删除：0=正常, 1=已删除             |


**索引说明**：

- 主键索引：`PRIMARY KEY (id)`
- 唯一索引：`UNIQUE INDEX uk_asset_sn (asset_sn)` - 防止重复录入
- 普通索引：`INDEX idx_user_id (user_id)` - 快速查询用户持有的资产
- 普通索引：`INDEX idx_status (status)` - 快速按状态筛选
- 普通索引：`INDEX idx_category (category)` - 快速按分类筛选

**状态枚举说明**：

- `0` - 闲置：资产在库，未被领用
- `1` - 领用中：资产已被员工领用
- `2` - 维修：资产正在维修中
- `3` - 报废：资产已报废

---

### 3.4 资产流转记录表 (`bus_record`)

记录资产的每一次申领、归还、维修等操作流水，用于追溯和审计。


| 字段名          | 类型          | 约束       | 默认值               | 备注                                                          |
| ------------ | ----------- | -------- | ----------------- | ----------------------------------------------------------- |
| id           | BIGINT      | PK, AI   | -                 | 主键 ID                                                       |
| asset_id     | BIGINT      | NOT NULL | -                 | 关联资产 ID（关联 bus_asset.id）                                    |
| user_id      | BIGINT      | NOT NULL | -                 | 操作人 ID（发起人，关联 sys_user.id）                                  |
| action_type  | VARCHAR(20) | NOT NULL | -                 | 动作类型：CLAIM(领用), RETURN(退库), REPAIR(报修), ADD(新增), DELETE(删除) |
| old_status   | TINYINT     | -        | NULL              | 变更前状态：0=闲置, 1=领用, 2=维修                                      |
| new_status   | TINYINT     | -        | NULL              | 变更后状态：同上                                                    |
| audit_status | TINYINT     | -        | 0                 | 审核状态：0=待审核, 1=已通过, 2=已驳回                                    |
| remark       | TEXT        | -        | NULL              | 操作备注（申请理由或审批意见）                                             |
| create_time  | DATETIME    | -        | CURRENT_TIMESTAMP | 记录创建时间                                                      |


**索引说明**：

- 主键索引：`PRIMARY KEY (id)`
- 普通索引：`INDEX idx_asset_id (asset_id)` - 快速查询某资产的所有流转记录
- 普通索引：`INDEX idx_user_id (user_id)` - 快速查询某用户的所有操作记录

**动作类型枚举说明**：

- `CLAIM` - 领用：员工申请领用资产
- `RETURN` - 退库：员工归还资产
- `REPAIR` - 报修：资产报修申请
- `ADD` - 新增：管理员新增资产
- `DELETE` - 删除：管理员删除资产（逻辑删除）

**审核状态枚举说明**：

- `0` - 待审核：操作已提交，等待管理员审批
- `1` - 已通过：管理员审批通过
- `2` - 已驳回：管理员审批驳回

---

## 4. 表关系图 (ER Diagram)

```
┌─────────────┐         ┌─────────────┐
│  sys_role   │         │  sys_user   │
├─────────────┤         ├─────────────┤
│ id (PK)     │◄────────┤ role_id (FK)│
│ role_name   │         │ id (PK)     │
│ role_key    │         │ username    │
│ status      │         │ password    │
└─────────────┘         │ nickname    │
                        │ ...         │
                        └──────┬──────┘
                               │
                               │ user_id (FK)
                               │
                        ┌──────▼──────┐
                        │ bus_asset   │
                        ├─────────────┤
                        │ id (PK)     │
                        │ asset_name  │
                        │ asset_sn    │
                        │ user_id (FK)│
                        │ status      │
                        │ ...         │
                        └──────┬──────┘
                               │
                               │ asset_id (FK)
                               │
                        ┌──────▼──────┐
                        │ bus_record  │
                        ├─────────────┤
                        │ id (PK)     │
                        │ asset_id(FK)│
                        │ user_id (FK)│
                        │ action_type │
                        │ audit_status│
                        │ ...         │
                        └─────────────┘
```

**关系说明**：

1. **sys_role ← sys_user**：一个角色可以对应多个用户（一对多）
2. **sys_user ← bus_asset**：一个用户可以持有多个资产（一对多）
3. **bus_asset ← bus_record**：一个资产可以有多条流转记录（一对多）
4. **sys_user ← bus_record**：一个用户可以有多条操作记录（一对多）

---

## 5. 数据字典

### 5.1 状态枚举值

#### 资产状态 (`bus_asset.status`)

- `0` - 闲置
- `1` - 领用中
- `2` - 维修
- `3` - 报废

#### 审核状态 (`bus_record.audit_status`)

- `0` - 待审核
- `1` - 已通过
- `2` - 已驳回

#### 账号状态 (`sys_user.status`, `sys_role.status`)

- `0` - 禁用
- `1` - 正常

#### 逻辑删除标记 (`del_flag`)

- `0` - 未删除（正常）
- `1` - 已删除（逻辑删除）

### 5.2 动作类型枚举 (`bus_record.action_type`)

- `CLAIM` - 领用
- `RETURN` - 退库
- `REPAIR` - 报修
- `ADD` - 新增
- `DELETE` - 删除

---

## 6. 索引设计说明

### 6.1 主键索引

所有表均使用 `BIGINT` 类型的自增主键，保证唯一性和查询性能。

### 6.2 唯一索引

- `sys_user.username` - 保证登录账号唯一性
- `sys_role.role_key` - 保证角色标识唯一性
- `bus_asset.asset_sn` - 保证资产序列号唯一性

### 6.3 普通索引

- `sys_user.role_id` - 快速查询某角色的所有用户
- `bus_asset.user_id` - 快速查询某用户持有的所有资产
- `bus_asset.status` - 快速按状态筛选资产
- `bus_asset.category` - 快速按分类筛选资产
- `bus_record.asset_id` - 快速查询某资产的所有流转记录
- `bus_record.user_id` - 快速查询某用户的所有操作记录

---

## 7. 扩展性考虑

### 7.1 多租户支持

若未来需要支持多个分公司，可在表中引入 `tenant_id` 字段，实现数据隔离。

### 7.2 多级部门

通过新增 `sys_dept` 表，使用 `parent_id` 字段实现无限级部门树结构。

### 7.3 多语言字段

针对国际化需求，部分资产名称可考虑存储对应的英文/日文名称，或使用独立的 `sys_i18n` 表进行多语言映射。

### 7.4 审计日志

可考虑新增 `sys_audit_log` 表，记录所有敏感操作的详细日志，包括操作人、操作时间、IP地址等信息。

### 7.5 文件附件

资产可关联附件（如采购合同、保修单等），可新增 `bus_asset_file` 表存储文件信息。

---

## 8. 性能优化建议

1. **分页查询**：资产列表、流转记录等大数据量查询应使用分页，避免一次性加载过多数据。
2. **定期归档**：对于历史流转记录，可考虑定期归档到历史表，减少主表数据量。
3. **缓存策略**：角色信息、用户基本信息等不常变化的数据可考虑使用 Redis 缓存。
4. **读写分离**：在高并发场景下，可考虑主从复制，读操作走从库，写操作走主库。

---

## 9. 数据初始化

数据库初始化脚本位于 `sql/init_db.sql`，执行后会自动创建：

- 数据库 `office_flow`
- 所有数据表
- 初始角色数据（ADMIN、USER）
- 初始用户数据（admin、user01，默认密码：123456）

---

## 10. 维护说明

### 10.1 备份策略

建议定期备份数据库，可使用 MySQL 的 `mysqldump` 工具：

```bash
mysqldump -u root -p office_flow > backup_$(date +%Y%m%d).sql
```

### 10.2 数据清理

- 逻辑删除的数据可通过 `del_flag=1` 筛选
- 如需物理删除，建议先备份，再执行 `DELETE` 操作
- 历史流转记录建议定期归档，避免表数据过大

### 10.3 版本升级

数据库结构变更时，应：

1. 编写迁移脚本（Migration Script）
2. 在测试环境验证
3. 备份生产数据
4. 执行迁移脚本
5. 验证数据完整性

---

## 11. 注意事项

1. **密码安全**：所有密码必须使用 BCrypt 加密存储，不得明文存储。
2. **时间字段**：使用 `DATETIME` 类型，时区设置为 `Asia/Tokyo` 或 `Asia/Shanghai`。
3. **金额字段**：使用 `DECIMAL(12,2)` 类型，保证精度，避免浮点数误差。
4. **字符编码**：所有表必须使用 `utf8mb4` 字符集，支持完整的 Unicode 字符。
5. **逻辑删除**：删除操作应使用逻辑删除（设置 `del_flag=1`），而非物理删除。
6. **外键约束**：当前设计未使用数据库外键约束，由应用层保证数据一致性。

---

**文档版本**：v1.0  
**最后更新**：2026年