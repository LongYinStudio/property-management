package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 车位租用记录返回VO
 */
@Data
public class ParkingRentalVO {
    
    private Long id;
    
    private Long spaceId;
    
    private String spaceNumber;
    
    private Long userId;
    
    private String userName;
    
    private String startDate;
    
    private String endDate;
    
    private BigDecimal amount;
    
    private Integer status;
    
    private String payTime;
    
    private String createTime;
}
