package com.officeflow.backend.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AssetRecordVO {
    private Long id;

    // 资产信息
    private Long assetId;
    private String assetName; // 冗余资产名称，方便展示

    // 操作人信息
    private Long userId;
    private String userNickname; // 展示“谁”操作的

    // 动作信息
    private String actionType;   // CLAIM, RETURN 等
    private Integer oldStatus;
    private Integer newStatus;
    private String remark;
    private Integer auditStatus;

    private LocalDateTime createTime;
}