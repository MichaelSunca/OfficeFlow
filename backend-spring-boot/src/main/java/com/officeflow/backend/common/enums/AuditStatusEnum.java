package com.officeflow.backend.common.enums;

import lombok.Getter;

@Getter
public enum AuditStatusEnum {
    PENDING(0, "待审批"),
    PASSED(1, "已通过"),
    REJECTED(2, "已驳回");

    private final Integer code;
    private final String description;

    AuditStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据 code 获取描述文字
     */
    public static String getDescriptionByCode(Integer code) {
        for (AuditStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return "未知";
    }
}