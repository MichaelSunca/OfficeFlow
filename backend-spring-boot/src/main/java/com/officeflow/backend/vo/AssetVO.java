package com.officeflow.backend.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssetVO {
    private Long id;
    private String assetName;
    private String assetSn;
    private String category;
    private Integer status;
    private String location;

    // 来自 sys_user 表的信息
    private Long userId;
    private String userName;     // 登录名
    private String userNickname; // 显示名称

    private LocalDateTime createTime;
}