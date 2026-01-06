package com.officeflow.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * System User Entity
 * Maps to 'sys_user' table
 */
@Data
@TableName("sys_user") // Specifies the table name in MySQL
public class User {

    @TableId(type = IdType.AUTO) // Primary Key with Auto-Increment
    private Long id;

    private String username;

    /**
     * Store as BCrypt hashed string
     */
    private String password;

    private String nickname;

    /**
     * Role can be 'ADMIN' or 'USER'
     */
    private String role;

    /**
     * Automatically mapped to 'create_time' (CamelCase to Under_score)
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * Automatically mapped to 'update_time'
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * Status: 1-Active, 0-Disabled
     */
    private Integer status;
}