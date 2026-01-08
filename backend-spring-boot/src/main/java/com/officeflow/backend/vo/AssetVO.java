package com.officeflow.backend.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AssetVO {
    private Long id;
    private String assetName;
    private String assetSn;
    private String category;
    private Integer status;
    private String location;
    private BigDecimal price;
    private LocalDate purchaseDate;
    /**
     * 是否存在待处理的申请
     * 在 SQL 中通过判断该资产在 bus_record 中是否有 audit_status = 0 的记录来赋值
     */
    private Boolean isPending;

    // 来自 sys_user 表的信息
    private Long userId;
    private String userName;
    private String userNickname;

    private LocalDateTime createTime;
}