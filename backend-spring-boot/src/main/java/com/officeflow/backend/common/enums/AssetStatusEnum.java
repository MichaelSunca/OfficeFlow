package com.officeflow.backend.common.enums;

public enum AssetStatusEnum {
    IDLE(0, "闲置"),
    USING(1, "领用中"),
    REPAIR(2, "维修中"),
    SCRAP(3, "已报废");

    private final Integer code;
    private final String description;

    AssetStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() { return code; }
    public String getDescription() { return description; }

    public static String getDescriptionByCode(Integer code) {
        for (AssetStatusEnum status : AssetStatusEnum.values()) {
            if (status.getCode().equals(code)) {
                return status.getDescription();
            }
        }
        return "未知状态";
    }
}