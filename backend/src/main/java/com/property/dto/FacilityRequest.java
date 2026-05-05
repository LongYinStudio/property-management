package com.property.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 设备设施请求DTO
 */
@Data
public class FacilityRequest {

    @NotBlank(message = "设施名称不能为空")
    @Size(max = 100, message = "设施名称长度不能超过100位")
    private String name;

    @NotNull(message = "设施类型不能为空")
    @Min(value = 1, message = "设施类型不正确")
    @Max(value = 4, message = "设施类型不正确")
    private Integer type;

    @Size(max = 255, message = "位置长度不能超过255位")
    private String location;

    @Min(value = 0, message = "设施状态不正确")
    @Max(value = 1, message = "设施状态不正确")
    private Integer status;

    private LocalDate lastCheckDate;

    private LocalDate nextCheckDate;

    @Size(max = 500, message = "备注长度不能超过500位")
    private String remark;

    @AssertTrue(message = "下次检查日期不能早于上次检查日期")
    public boolean isNextCheckDateValid() {
        return nextCheckDate == null || lastCheckDate == null || !nextCheckDate.isBefore(lastCheckDate);
    }
}
