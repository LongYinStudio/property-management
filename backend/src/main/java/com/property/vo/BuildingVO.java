package com.property.vo;

import lombok.Data;

/**
 * 楼栋返回VO
 */
@Data
public class BuildingVO {

    private Long id;

    private Long communityId;

    private String communityName;

    private String name;

    private Integer floors;

    private Integer units;

    private Integer roomsPerFloor;

    private Long roomCount;

    private String createTime;

    private String updateTime;
}
