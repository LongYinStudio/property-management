package com.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 物业费请求DTO
 */
@Data
public class PropertyFeeRequest {
    
    @NotNull(message = "用户ID不能为空")
    @Positive(message = "用户ID必须大于0")
    private Long userId;
    
    @Positive(message = "房间ID必须大于0")
    private Long roomId;
    
    @NotNull(message = "年份不能为空")
    @Min(value = 2000, message = "年份不能早于2000")
    @Max(value = 9999, message = "年份格式不正确")
    private Integer year;
    
    @NotNull(message = "月份不能为空")
    @Min(value = 1, message = "月份必须在1-12之间")
    @Max(value = 12, message = "月份必须在1-12之间")
    private Integer month;
    
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01", message = "金额必须大于0")
    private BigDecimal amount;
    
    @NotNull(message = "费用类型不能为空")
    @Min(value = 1, message = "费用类型不正确")
    @Max(value = 4, message = "费用类型不正确")
    private Integer type;
    
    @Size(max = 255, message = "描述长度不能超过255位")
    private String description;
}
