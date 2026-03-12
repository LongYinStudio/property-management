package com.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物业费请求DTO
 */
@Data
public class PropertyFeeRequest {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    private Long roomId;
    
    @NotNull(message = "年份不能为空")
    private Integer year;
    
    @NotNull(message = "月份不能为空")
    private Integer month;
    
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    
    @NotNull(message = "费用类型不能为空")
    private Integer type;
    
    private String description;
}
