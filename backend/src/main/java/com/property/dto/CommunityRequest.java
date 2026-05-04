package com.property.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 小区请求DTO
 */
@Data
public class CommunityRequest {

    @NotBlank(message = "小区名称不能为空")
    @Size(max = 100, message = "小区名称长度不能超过100位")
    private String name;

    @Size(max = 255, message = "小区地址长度不能超过255位")
    private String address;

    @DecimalMin(value = "0.00", inclusive = false, message = "占地面积必须大于0")
    @Digits(integer = 10, fraction = 2, message = "占地面积格式不正确")
    private BigDecimal area;

    @Min(value = 1900, message = "建成年份不能早于1900年")
    @Max(value = 2100, message = "建成年份不能晚于2100年")
    private Integer buildYear;

    @Min(value = 0, message = "楼栋总数不能小于0")
    private Integer totalBuildings;

    @Min(value = 0, message = "房间总数不能小于0")
    private Integer totalRooms;

    @Size(max = 2000, message = "小区描述长度不能超过2000位")
    private String description;
}
