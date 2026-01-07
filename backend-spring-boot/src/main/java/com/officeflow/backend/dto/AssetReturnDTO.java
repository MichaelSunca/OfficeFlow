package com.officeflow.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssetReturnDTO {
    @NotNull(message = "退库资产ID不能为空")
    private Long assetId;

    private String remark;
}