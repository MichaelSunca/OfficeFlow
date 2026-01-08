package com.officeflow.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("bus_record")
public class AssetRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assetId;

    private Long userId;

    /**
     * 动作类型: APPLY, RETURN, REPAIR
     */
    private String actionType;

    /**
     * 变更前状态
     */
    private Integer oldStatus;

    /**
     * 变更后状态
     */
    private Integer newStatus;

    private String remark;

    /**
     * 审批状态: 0=待审, 1=通过, 2=拒绝
     */
    private Integer auditStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}