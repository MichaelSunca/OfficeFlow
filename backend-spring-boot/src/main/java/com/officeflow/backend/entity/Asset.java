package com.officeflow.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 资产台账实体类
 * 对应数据库表：bus_asset
 */
@Data
@TableName("bus_asset")
public class Asset {

    /**
     * 主键 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 资产序列号/机身码 (唯一标识)
     */
    private String assetSn;

    /**
     * 分类: 如 IT设备, 办公家具, 行政用品
     */
    private String category;

    /**
     * 采购价格 (使用 BigDecimal 保证金额精度)
     */
    private BigDecimal price;

    /**
     * 资产状态: 0=闲置, 1=领用中, 2=维修, 3=报废
     */
    private Integer status;

    /**
     * 当前持有者/领用人 ID
     */
    private Long userId;

    /**
     * 存放地点
     */
    private String location;

    /**
     * 采购日期
     */
    private LocalDate purchaseDate;

    /**
     * 创建者账号 (MyBatis-Plus 自动填充)
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新者账号 (MyBatis-Plus 自动填充)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记: 0=未删除, 1=已删除
     */
    @TableLogic
    private Integer delFlag;
}