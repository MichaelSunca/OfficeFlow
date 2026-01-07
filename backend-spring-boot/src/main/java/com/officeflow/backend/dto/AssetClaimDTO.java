package com.officeflow.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 资产领用请求参数
 */
@Data
public class AssetClaimDTO {

    @NotBlank(message = "领用资产ID不能为空")
    private Long assetId;

    /**
     * 领用备注或原因
     */
    private String remark;
}