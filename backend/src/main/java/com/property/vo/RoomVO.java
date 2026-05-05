package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 房屋返回VO
 */
@Data
public class RoomVO {

    private Long id;

    private Long communityId;

    private String communityName;

    private Long buildingId;

    private String buildingName;

    private String unit;

    private Integer floor;

    private String roomNumber;

    private String displayName;

    private BigDecimal area;

    private Long ownerId;

    private String ownerName;

    private Integer status;

    private String createTime;

    private String updateTime;

    public String getStatusName() {
        return switch (status == null ? -1 : status) {
            case 0 -> "空置";
            case 1 -> "已入住";
            default -> "未知";
        };
    }
}
