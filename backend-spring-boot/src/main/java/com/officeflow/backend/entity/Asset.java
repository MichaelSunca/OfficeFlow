package com.officeflow.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产实体类，对应数据库 'bus_asset' 表
 * 用于存储办公设备、办公用品等核心信息
 */
@Data
@TableName("bus_asset")
public class Asset {

    /**
     * 主键 ID，设置为自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 资产名称 (例如: MacBook Pro, 办公椅)
     */
    private String assetName;

    /**
     * 唯一序列号或条形码，用于实物追踪
     */
    @NotBlank(message = "资产序列号不能为空")
    private String assetSn;

    /**
     * 资产分类 (例如: 电子设备, 办公家具, 行政耗材)
     */
    private String category;

    /**
     * 采购价格，使用 BigDecimal 确保金额计算精准（无损精度）
     */
    private BigDecimal price;

    /**
     * 当前状态：0=闲置, 1=使用中, 2=维修中, 3=已报废
     */
    private Integer status;

    /**
     * 外键：当前持有者 ID (关联 sys_user 表的 id)
     */
    private Long userId;

    /**
     * 资产存放的物理地点 (例如: 302会议室, 仓库A)
     */
    private String location;

    /**
     * 购买日期
     */
    private LocalDate purchaseDate;

    /**
     * 审计字段：记录创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 审计字段：最后一次更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}