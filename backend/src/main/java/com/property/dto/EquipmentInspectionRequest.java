package com.property.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 设备巡检记录请求DTO
 */
@Data
public class EquipmentInspectionRequest {

    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;

    @NotNull(message = "设备类型不能为空")
    private Integer equipmentType;

    @NotBlank(message = "设备位置不能为空")
    private String location;

    @NotNull(message = "巡检日期不能为空")
    private LocalDate inspectionDate;

    private LocalDate nextInspectionDate;

    @NotNull(message = "巡检状态不能为空")
    private Integer status;

    @NotBlank(message = "巡检结果不能为空")
    private String result;

    private String issueDescription;

    private String images;

    private String remark;
}
