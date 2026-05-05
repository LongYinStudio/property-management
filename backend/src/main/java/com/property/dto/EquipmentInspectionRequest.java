package com.property.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * 设备巡检记录请求DTO
 */
@Data
public class EquipmentInspectionRequest {

    @NotNull(message = "设施不能为空")
    @Positive(message = "设施ID必须大于0")
    private Long facilityId;

    @NotNull(message = "巡检日期不能为空")
    private LocalDate inspectionDate;

    private LocalDate nextInspectionDate;

    @NotNull(message = "巡检状态不能为空")
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
