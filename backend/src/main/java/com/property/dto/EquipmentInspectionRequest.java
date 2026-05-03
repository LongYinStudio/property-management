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
 * 设备巡检记录请求DTO
 */
@Data
public class EquipmentInspectionRequest {

    @NotBlank(message = "设备名称不能为空")
    @Size(max = 100, message = "设备名称长度不能超过100位")
    private String equipmentName;

    @NotNull(message = "设备类型不能为空")
    @Min(value = 1, message = "设备类型不正确")
    @Max(value = 6, message = "设备类型不正确")
    private Integer equipmentType;

    @NotBlank(message = "设备位置不能为空")
    @Size(max = 255, message = "设备位置长度不能超过255位")
    private String location;

    @NotNull(message = "巡检日期不能为空")
    private LocalDate inspectionDate;

    private LocalDate nextInspectionDate;

    @NotNull(message = "巡检状态不能为空")
    @Min(value = 0, message = "巡检状态不正确")
    @Max(value = 2, message = "巡检状态不正确")
    private Integer status;

    @NotBlank(message = "巡检结果不能为空")
    @Size(max = 1000, message = "巡检结果长度不能超过1000位")
    private String result;

    @Size(max = 1000, message = "问题描述长度不能超过1000位")
    private String issueDescription;

    @Size(max = 1000, message = "图片地址长度不能超过1000位")
    private String images;

    @Size(max = 500, message = "备注长度不能超过500位")
    private String remark;

    @AssertTrue(message = "下次巡检日期不能早于巡检日期")
    public boolean isNextInspectionDateValid() {
        return nextInspectionDate == null || inspectionDate == null || !nextInspectionDate.isBefore(inspectionDate);
    }
}
