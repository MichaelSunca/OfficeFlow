# OfficeFlow 数据库设计文档 (v1.0)

## 1. 设计原则

* **命名规范**：系统表以 `sys_` 开头，业务表以 `bus_` 开头。
* **字符集**：使用 `utf8mb4` 以支持多语言（包括日语汉字、假名及 Emoji）。
* **通用字段**：核心表均包含 `create_time` 和 `update_time` 以便审计。
* **逻辑删除**：关键业务数据建议使用逻辑删除字段 `del_flag`。

---

## 2. 实体关系图 (ERD)

---

## 3. 表结构详细说明

### 3.1 系统用户表 (`sys_user`)

存储员工账户、登录凭证及基本身份信息。

| 字段名 | 类型 | 约束 | 备注 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AI | 主键 ID |
| username | VARCHAR(50) | UNIQUE, NOT NULL | 登录账号/工号 |
| password | VARCHAR(100) | NOT NULL | BCrypt 加密密码 |
| nickname | VARCHAR(50) | - | 员工真实姓名 |
| role | VARCHAR(20) | DEFAULT 'USER' | 角色：ADMIN, USER |
| status | TINYINT | DEFAULT 1 | 状态：1启用, 0禁用 |
| dept_id | BIGINT | - | 所属部门 ID（预留） |
| create_time | DATETIME | - | 创建时间 |
| update_time | DATETIME | - | 更新时间 |

### 3.2 资产信息表 (`bus_asset`) —— **待创建**

存储办公设备的核心静态信息及当前状态。

| 字段名 | 类型 | 约束 | 备注 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AI | 主键 ID |
| asset_name | VARCHAR(100) | NOT NULL | 资产名称 (如: MacBook Pro) |
| asset_sn | VARCHAR(50) | UNIQUE | 唯一序列号/条码 |
| category | VARCHAR(30) | - | 分类 (如: 电子设备, 办公家具) |
| price | DECIMAL(10,2) | - | 采购价格 |
| status | TINYINT | DEFAULT 0 | 0:闲置, 1:使用中, 2:维修, 3:报废 |
| user_id | BIGINT | FK | 当前持有者 ID (关联 sys_user.id) |
| location | VARCHAR(100) | - | 存放地点 |
| purchase_date | DATE | - | 购买日期 |

### 3.3 资产流转记录表 (`bus_record`) —— **待创建**

记录资产的每一次申领、归还、维修流水，用于追溯。

| 字段名 | 类型 | 约束 | 备注 |
| --- | --- | --- | --- |
| id | BIGINT | PK, AI | 主键 ID |
| asset_id | BIGINT | NOT NULL | 关联资产 ID |
| user_id | BIGINT | NOT NULL | 操作人 ID |
| action_type | VARCHAR(20) | - | 操作类型: APPLY(申领), RETURN(归还), REPAIR(报废) |
| remark | TEXT | - | 申请理由或备注 |
| audit_status | TINYINT | DEFAULT 0 | 0:待审批, 1:已通过, 2:已拒绝 |
| create_time | DATETIME | - | 记录产生时间 |

---

## 4. 扩展性考虑

1. **多租户支持**：若未来需要支持多个分公司，可在表中引入 `tenant_id`。
2. **多级部门**：通过 `sys_dept` 表的 `parent_id` 实现无限级部门树。
3. **多语言字段**：针对日本办公环境，部分资产名称可考虑存储对应的英文/日文名称。

