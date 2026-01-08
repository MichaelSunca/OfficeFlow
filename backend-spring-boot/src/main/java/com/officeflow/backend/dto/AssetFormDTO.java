package com.officeflow.backend.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 资产表单传输对象
 * 用于：资产新增、资产编辑
 */
@Data
public class AssetFormDTO {

    // 💡 校验分组：方案 A (定义在内部)
    // 继承 Default 后，未标注 groups 的字段会默认属于这两个分组
    public interface CreateGroup extends Default {}
    public interface UpdateGroup extends Default {}

    @NotNull(message = "编辑操作必须传入资产ID", groups = UpdateGroup.class)
    @Null(message = "新增操作不能手动传入ID", groups = CreateGroup.class)
    private Long id;

    @NotBlank(message = "资产名称不能为空")
    @Size(max = 100, message = "资产名称长度不能超过100个字符")
    private String assetName;

    @NotBlank(message = "资产序列号(SN)不能为空")
    @Pattern(regexp = "^[A-Za-z0-9-]+$", message = "序列号只能包含字母、数字和中划线")
    private String assetSn;

    @NotBlank(message = "请选择资产分类")
    private String category;

    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.0", message = "资产价格不能为负数")
    private BigDecimal price;

    @Size(max = 100, message = "存放地点描述过长")
    private String location;

    @PastOrPresent(message = "采购日期不能晚于今天")
    private LocalDate purchaseDate;
}