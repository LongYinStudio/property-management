package com.property.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 物业费返回VO
 */
@Data
public class PropertyFeeVO {
    
    private Long id;
    
    private Long userId;
    
    private String userName;
    
    private Long roomId;
    
    private Integer year;
    
    private Integer month;
    
    private BigDecimal amount;
    
    private Integer type;
    
    private Integer status;
    
    private String description;
    
    private String payTime;
    
    private String createTime;
}
