package com.officeflow.backend.dto;

import lombok.Data;

@Data
public class AssetAuditDTO {
    private Long recordId;
    private Integer auditResult;
    private String auditRemark;
}