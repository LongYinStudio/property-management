package com.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 车位请求DTO
 */
@Data
public class ParkingSpaceRequest {
    
    @NotBlank(message = "车位编号不能为空")
    @Size(max = 20, message = "车位编号长度不能超过20位")
    private String spaceNumber;
    
    @Size(max = 100, message = "车位位置长度不能超过100位")
    private String location;
    
    @NotNull(message = "车位类型不能为空")
    @Min(value = 1, message = "车位类型不正确")
    @Max(value = 2, message = "车位类型不正确")
    private Integer type;
    
    @NotNull(message = "月租价格不能为空")
    @DecimalMin(value = "0.01", message = "月租价格必须大于0")
    private BigDecimal rentPrice;
    
    @DecimalMin(value = "0.00", message = "出售价格不能小于0")
    private BigDecimal price;
    
    @Size(max = 255, message = "备注长度不能超过255位")
    private String remark;
}
