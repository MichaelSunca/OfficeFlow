package com.officeflow.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetAddDTO {

    @NotBlank(message = "资产名称不能为空")
    private String assetName;

    @NotBlank(message = "资产序列号(SN)不能为空")
    private String assetSn;

    private String category;

    @NotNull(message = "采购价格不能为空")
    private BigDecimal price;

    private String location;

    private LocalDate purchaseDate;
}