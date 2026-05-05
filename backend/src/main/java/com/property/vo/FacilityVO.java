package com.property.vo;

import lombok.Data;

/**
 * 设备设施返回VO
 */
@Data
public class FacilityVO {

    private Long id;

    private String name;

    private Integer type;

    private String location;

    private Integer status;

    private String lastCheckDate;

    private String nextCheckDate;

    private String remark;

    private String createTime;

    private String updateTime;

    public String getTypeName() {
        return switch (type == null ? -1 : type) {
            case 1 -> "消防设施";
            case 2 -> "电梯";
            case 3 -> "监控";
            case 4 -> "其他";
            default -> "未知";
        };
    }

    public String getStatusName() {
        return switch (status == null ? -1 : status) {
            case 0 -> "故障";
            case 1 -> "正常";
            default -> "未知";
        };
    }
}
