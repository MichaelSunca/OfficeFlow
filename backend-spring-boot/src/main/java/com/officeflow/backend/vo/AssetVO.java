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

    // 来自 sys_user 表的信息
    private Long userId;
    private String userName;
    private String userNickname;

    private LocalDateTime createTime;
}