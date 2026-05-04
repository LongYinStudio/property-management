package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 小区返回VO
 */
@Data
public class CommunityVO {

    private Long id;

    private String name;

    private String address;

    private BigDecimal area;

    private Integer buildYear;

    private Integer totalBuildings;

    private Integer totalRooms;

    private String description;

    private String createTime;

    private String updateTime;
}
