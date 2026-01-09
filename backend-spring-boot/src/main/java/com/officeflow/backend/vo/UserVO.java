package com.officeflow.backend.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String password; // 仅内部认证使用
    private String nickname;
    private Long roleId;
    private String roleKey;  // 对应 r.role_key，存放 ADMIN/USER
    private String roleName; // 对应 r.role_name，存放 管理员/普通员工
    private Integer status; // 对应 r.role_name，存放 管理员/普通员工
}