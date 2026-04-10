package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 车位返回VO
 */
@Data
public class ParkingSpaceVO {
    
    private Long id;
    
    private String spaceNumber;
    
    private String location;
    
    private Integer type;
    
    private Integer status;
    
    private Long ownerId;
    
    private String ownerName;
    
    private BigDecimal price;
    
    private BigDecimal rentPrice;
    
    private String remark;
    
    private String createTime;
}
