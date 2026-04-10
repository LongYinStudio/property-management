package com.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 车位请求DTO
 */
@Data
public class ParkingSpaceRequest {
    
    @NotBlank(message = "车位编号不能为空")
    private String spaceNumber;
    
    private String location;
    
    @NotNull(message = "车位类型不能为空")
    private Integer type;
    
    @NotNull(message = "月租价格不能为空")
    @DecimalMin(value = "0.01", message = "月租价格必须大于0")
    private BigDecimal rentPrice;
    
    private BigDecimal price;
    
    private String remark;
}
