package com.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 房屋请求DTO
 */
@Data
public class RoomRequest {

    @NotNull(message = "楼栋不能为空")
    @Positive(message = "楼栋ID必须大于0")
    private Long buildingId;

    @Size(max = 10, message = "单元长度不能超过10位")
    private String unit;

    @Min(value = 1, message = "楼层不能小于1")
    @Max(value = 200, message = "楼层不能大于200")
    private Integer floor;

    @NotBlank(message = "房号不能为空")
    @Size(max = 20, message = "房号长度不能超过20位")
    private String roomNumber;

    @DecimalMin(value = "0.00", inclusive = false, message = "面积必须大于0")
    @Digits(integer = 10, fraction = 2, message = "面积格式不正确")
    private BigDecimal area;

    @Min(value = 0, message = "房屋状态不正确")
    @Max(value = 1, message = "房屋状态不正确")
    private Integer status;
}
